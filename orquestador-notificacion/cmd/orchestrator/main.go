package main

import (
	"context"
	"fmt"
	"log"
	"os"
	"os/signal"
	"syscall"
	"time"

	"github.com/andrew/orquestador-notificacion/internal/config"
	"github.com/andrew/orquestador-notificacion/internal/handler"
	kafkaPkg "github.com/andrew/orquestador-notificacion/internal/kafka"
	"github.com/andrew/orquestador-notificacion/internal/logger"
	"github.com/andrew/orquestador-notificacion/internal/processor"
	"github.com/andrew/orquestador-notificacion/internal/service"
	kafka "github.com/segmentio/kafka-go"
	"go.uber.org/zap"
)

func main() {
	ctx, cancel := context.WithCancel(context.Background())
	defer cancel()

	// load config
	cfg := config.LoadFromEnv()

	// logger
	z, err := logger.NewLogger()
	if err != nil {
		log.Fatalf("failed to create logger: %v", err)
	}
	defer func(z *zap.Logger) {
		_ = z.Sync()
	}(z)

	// Verificar conectividad con Kafka antes de iniciar
	z.Info("checking kafka connectivity...", zap.Strings("brokers", cfg.KafkaBrokers))
	if err := checkKafkaConnectivity(cfg.KafkaBrokers); err != nil {
		z.Fatal("kafka not available", zap.Error(err))
	}
	z.Info("kafka connectivity confirmed")

	// Crear producer para topic de notificaciones
	producerTopic := getEnv("KAFKA_PRODUCER_TOPIC", "notifications")
	producer := kafkaPkg.NewProducer(cfg.KafkaBrokers, producerTopic)

	// infra: registry, services, handlers
	reg := handler.NewRegistry()
	userSvc := service.NewUserService(producer, z) // ⬅️ ahora con producer y logger

	// Registrar handlers
	reg.Register(handler.NewUserRegisteredHandler(userSvc, z))
	reg.Register(handler.NewUserLoginHandler(userSvc, z))
	reg.Register(handler.NewPasswordChangedHandler(userSvc, z))
	reg.Register(handler.NewOtpRequestedHandler(userSvc, z))

	proc := processor.NewProcessor(reg, z)

	// kafka reader config - escucha el topic user-events
	rCfg := kafka.ReaderConfig{
		Brokers:  cfg.KafkaBrokers,
		Topic:    cfg.KafkaTopic,
		GroupID:  cfg.GroupID,
		MinBytes: 10e3, // 10KB
		MaxBytes: 10e6, // 10MB
	}

	consumer := kafkaPkg.NewConsumer(rCfg, proc, z)

	// Iniciar consumer con manejo de panics
	go func() {
		defer func() {
			if r := recover(); r != nil {
				z.Error("consumer panic recovered", zap.Any("panic", r))
			}
		}()
		consumer.Start(ctx, 4) // 4 workers
	}()

	// graceful shutdown
	sig := make(chan os.Signal, 1)
	signal.Notify(sig, os.Interrupt, syscall.SIGTERM)
	<-sig
	z.Info("shutdown requested")
	cancel()

	// allow some time to finish
	time.Sleep(3 * time.Second)
	if err := consumer.Close(); err != nil {
		z.Error("error closing consumer", zap.Error(err))
	}
	if err := producer.Close(); err != nil { // ⬅️ cerramos producer también
		z.Error("error closing producer", zap.Error(err))
	}
	z.Info("bye")
}

// Función para verificar conectividad con Kafka - acepta []string
func checkKafkaConnectivity(brokers []string) error {
	if len(brokers) == 0 {
		return fmt.Errorf("no kafka brokers configured")
	}

	conn, err := kafka.Dial("tcp", brokers[0])
	if err != nil {
		return fmt.Errorf("failed to connect to broker %s: %w", brokers[0], err)
	}
	defer func(conn *kafka.Conn) {
		_ = conn.Close()
	}(conn)

	_, err = conn.Brokers()
	if err != nil {
		return fmt.Errorf("failed to get brokers list: %w", err)
	}

	return nil
}

func getEnv(key, fallback string) string {
	if v := os.Getenv(key); v != "" {
		return v
	}
	return fallback
}
