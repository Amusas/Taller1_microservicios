package config

type Config struct {
	KafkaBrokers []string
	KafkaTopic   string
	GroupID      string
}

func LoadFromEnv() Config {
	// Por simplicidad: valores hardcodeados o leer env vars aquí
	return Config{
		KafkaBrokers: []string{"localhost:29092"},
		KafkaTopic:   "user-events",
		GroupID:      "kafka-listener-group",
	}

}
