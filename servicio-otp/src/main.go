package main

import (
	"log"
	"net/http"

	"servicio-otp/handlers"

	"github.com/gorilla/mux"
)

func main() {
	router := mux.NewRouter()

	// Rutas RESTful
	router.HandleFunc("/api/otp", handlers.CreateOtp).Methods("POST")
	router.HandleFunc("/api/otp/check", handlers.CheckOtp).Methods("POST")

	log.Println("🚀 OTP Service running on port 8083")
	log.Fatal(http.ListenAndServe(":8083", router))
}
