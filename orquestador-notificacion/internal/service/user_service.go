package service

import (
	"context"

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
	data := map[string]interface{}{
		"user_id": id,
		"name":    name,
		"phone":   phone,
	}

	err := s.producer.SendEvent(ctx, "EMAIL", "validate_account", email, data)
	if err != nil {
		s.logger.Error("failed to send notification", zap.Error(err))
		return err
	}

	s.logger.Info("notification event published",
		zap.String("type", "EMAIL"),
		zap.String("template", "validate_account"),
		zap.String("to", email),
	)
	return nil
}

func (s *userServiceImpl) SendNotification(ctx context.Context, id int, email, name, phone, channel, template string) error {
	to := chooseTarget(channel, email, phone)
	data := map[string]interface{}{
		"user_id": id,
		"name":    name,
		"phone":   phone,
	}

	err := s.producer.SendEvent(ctx, channel, template, to, data)
	if err != nil {
		s.logger.Error("failed to send notification", zap.Error(err))
		return err
	}

	s.logger.Info("notification sent",
		zap.String("channel", channel),
		zap.String("to", to),
	)
	return nil
}

func chooseTarget(channel, email, phone string) string {
	if channel == "EMAIL" {
		return email
	}
	return phone
}

func (s *userServiceImpl) SendOtpRecovery(ctx context.Context, id int, email, name, url string) error {
	data := map[string]interface{}{
		"user_id": id,
		"name":    name,
		"url":     url,
	}

	err := s.producer.SendEvent(ctx, "EMAIL", "password_recovery", email, data)
	if err != nil {
		s.logger.Error("failed to send OTP recovery notification", zap.Error(err))
		return err
	}

	s.logger.Info("OTP recovery notification sent",
		zap.String("to", email),
	)
	return nil
}
