package service

import (
	"context"
	"encoding/json"
	"fmt"
	"go.uber.org/zap"
	"strconv"
)

type UserService interface {
	OnUserRegistered(ctx context.Context, id int, email, name, phone string) error
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
		"type": "SEND_EMAIL",
		"data": map[string]interface{}{
			"to":       email,
			"template": "confirm_account",
			"variables": map[string]string{
				"userName": name,
				"id":       strconv.Itoa(id),
			},
		},
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
