package service

import "context"

type Producer interface {
	Send(ctx context.Context, key []byte, value []byte) error
}
