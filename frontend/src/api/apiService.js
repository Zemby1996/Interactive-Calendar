

import axios from 'axios';


const apiService = axios.create({
    baseURL: 'https://kalendarz-backend-ca1y.onrender.com/api' // Upewnij się, że port jest poprawny
});

// Ten fragment kodu "przechwytuje" każde zapytanie WYCHODZĄCE z naszej aplikacji.
apiService.interceptors.request.use(
    (config) => {
        
        const token = localStorage.getItem('token');
        
        
        if (token) {
            config.headers['Authorization'] = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);


// Teraz definiujemy proste funkcje dla każdego endpointu w naszym backendzie.

// Auth
export const registerUser = (userData) => apiService.post('/auth/register', userData);
export const loginUser = (credentials) => apiService.post('/auth/login', credentials);

// Doctors
export const getAllDoctors = () => apiService.get('/doctors');
export const getAvailableSlots = (doctorId, date) => apiService.get(`/doctors/${doctorId}/available-slots?date=${date}`);

// Appointments
export const createAppointment = (appointmentData) => apiService.post('/appointments', appointmentData);
export const getMyApointments = () => apiService.get('/appointments/me');
export const updateAppointment = (appointmentId, appointmentData) => apiService.put(`/appointments/${appointmentId}`, appointmentData);
export const deleteAppointment = (appointmentId) => apiService.delete(`/appointments/${appointmentId}`);
export const getAppointments = (doctorId, start, end) => apiService.get(`/doctors/${doctorId}/appointments?start=${start}&end=${end}`);
export const getPublicAppointments = (doctorId, start, end) => apiService.get(`/doctors/${doctorId}/appointments?start=${start}&end=${end}`);
export const getAllAppointments = () => apiService.get('/appointments/all');

export default apiService;