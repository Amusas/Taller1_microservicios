package handlers

import (
	"encoding/json"
	"net/http"
	"servicio-otp/models"
	"servicio-otp/services"
)

func CreateOtp(w http.ResponseWriter, r *http.Request) {
	otp := services.GenerateOtp()

	response := models.OtpCreationResponse{Otp: otp}

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(response)
}

func CheckOtp(w http.ResponseWriter, r *http.Request) {
	var req models.CheckOtpFormatRequest
	err := json.NewDecoder(r.Body).Decode(&req)
	if err != nil {
		http.Error(w, "Invalid request format", http.StatusBadRequest)
		return
	}

	isValid := services.ValidateOtp(req.Otp)

	response := models.CheckOtpFormatResponse{IsValidOtp: isValid}

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(response)
}
