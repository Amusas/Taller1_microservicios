package services

import (
	"fmt"
	"math/rand"
	"time"
)

// Generar OTP de 6 dígitos
func GenerateOtp() string {
	rand.Seed(time.Now().UnixNano())
	return formatOtp(rand.Intn(1000000))
}

func formatOtp(num int) string {
	return formatWithZeros(num, 6)
}

func formatWithZeros(num, length int) string {
	return formatString(num, "%0*d", length)
}

func formatString(num int, format string, length int) string {
	return sprintf(format, length, num)
}

func sprintf(format string, a ...interface{}) string {
	return fmt.Sprintf(format, a...)
}

// Validar si el OTP tiene exactamente 6 dígitos numéricos
func ValidateOtp(otp string) bool {
	if len(otp) != 6 {
		return false
	}
	for _, c := range otp {
		if c < '0' || c > '9' {
			return false
		}
	}
	return true
}
