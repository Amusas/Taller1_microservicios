package service

import (
	"context"
	"encoding/json"
	"fmt"
	"go.uber.org/zap"
)

type UserService interface {
	OnUserRegistered(ctx context.Context, id int, email, name, phone string) error
	// En tu service.UserService (ejemplo de firma nueva)
	SendNotification(ctx context.Context, id int, email, name, phone, channel, template string) error
	// Nuevo método especializado para OTP
	SendOtpRecovery(ctx context.Context, id int, email, name, url string) error
}

type userServiceImpl struct {
	producer Producer // usa la interfaz, no la implementación concreta
	logger   *zap.Logger
}

func NewUserService(producer Producer, logger *zap.Logger) UserService {
	return &userServiceImpl{producer: producer, logger: logger}
}

func (s *userServiceImpl) OnUserRegistered(ctx context.Context, id int, email, name, phone string) error {
	notif := map[string]interface{}{
		"user_id":  id,
		"email":    email,
		"name":     name,
		"channel":  "EMAIL",
		"template": "validate_account",
	}

	payload, err := json.Marshal(notif)
	if err != nil {
		return fmt.Errorf("failed to marshal notification: %w", err)
	}

	if err := s.producer.Send(ctx, []byte(email), payload); err != nil {
		s.logger.Error("failed to send notification", zap.Error(err))
		return err
	}

	s.logger.Info("notification event published", zap.String("email", email))
	return nil
}

func (s *userServiceImpl) SendNotification(ctx context.Context, id int, email, name, phone, channel, template string) error {
	notif := map[string]interface{}{
		"user_id":  id,
		"email":    email,
		"name":     name,
		"channel":  channel,
		"template": "password_recovery",
	}

	payload, err := json.Marshal(notif)
	if err != nil {
		return fmt.Errorf("failed to marshal notification: %w", err)
	}

	if err := s.producer.Send(ctx, []byte(email), payload); err != nil {
		s.logger.Error("failed to send notification", zap.Error(err))
		return err
	}

	s.logger.Info("notification sent", zap.String("channel", channel), zap.String("to", fmt.Sprintf("%s", notif["data"].(map[string]interface{})["to"])))
	return nil
}

func chooseTarget(channel, email, phone string) string {
	if channel == "EMAIL" {
		return email
	}
	return phone
}

func (s *userServiceImpl) SendOtpRecovery(ctx context.Context, id int, email, name, url string) error {
	event := map[string]interface{}{
		"type":     "PASSWORD_RECOVERY",
		"user_id":  id,
		"email":    email,
		"name":     name,
		"channel":  "EMAIL",
		"template": "password_recovery",
		"data": map[string]string{
			"url":  url,
			"name": name,
		},
	}

	payload, err := json.Marshal(event)
	if err != nil {
		return fmt.Errorf("failed to marshal notification: %w", err)
	}

	if err := s.producer.Send(ctx, []byte(email), payload); err != nil {
		s.logger.Error("failed to send notification", zap.Error(err))
		return err
	}

	s.logger.Info("notification sent")
	return nil
}
