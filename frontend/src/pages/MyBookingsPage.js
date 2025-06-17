// src/pages/MyBookingsPage.js
import React, { useState, useEffect, useContext } from 'react';
import { getMyApointments, deleteAppointment, updateAppointment } from '../api/apiService';
import AuthContext from '../context/AuthContext';
import { format } from 'date-fns';
import { pl } from 'date-fns/locale';
import './MyBookingsPage.css';
import { toast } from 'react-toastify';
import Spinner from '../components/Spinner';

const MyBookingsPage = () => {
    const [bookings, setBookings] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState(null);
    const { auth } = useContext(AuthContext);

    // 1. Efekt, który pobiera wizyty użytkownika po załadowaniu strony
    useEffect(() => {
        const fetchBookings = async () => {
            if (!auth.token) {
                // Jeśli z jakiegoś powodu nie ma tokenu, nie wykonuj zapytania
                setIsLoading(false);
                return;
            }

            try {
                const response = await getMyApointments();
                setBookings(response.data.data);
            } catch (err) {
                console.error("Błąd podczas pobierania wizyt:", err);
                setError("Nie udało się pobrać Twoich wizyt. Spróbuj ponownie później.");
            } finally {
                setIsLoading(false);
            }
        };

        fetchBookings();
    }, [auth.token]);

    // 2. Funkcja do obsługi anulowania wizyty
    const handleCancelBooking = async (appointmentId) => {
        if (window.confirm('Czy na pewno chcesz anulować tę wizytę?')) {
            try {
                await deleteAppointment(appointmentId);
                setBookings(prevBookings => prevBookings.filter(booking => booking.id !== appointmentId));
                toast.success('Wizyta została pomyślnie anulowana!'); // 2. Zastępujemy alert
            } catch (err) {
                console.error("Błąd podczas anulowania wizyty:", err);
                toast.error("Nie udało się anulować wizyty."); // 3. Zastępujemy alert błędu
            }
        }
    };


    // 3. Logika renderowania komponentu
    if (isLoading) {
        return <Spinner />;
    }

    if (error) {
        return <div className="error-message">{error}</div>;
    }

    if (bookings.length === 0) {
        return <div className="info-message">Nie masz jeszcze żadnych zarezerwowanych wizyt.</div>;
    }

    return (
        <div className="my-bookings-container">
            <h1>Moje Wizyty</h1>
            <div className="bookings-list">
                {bookings.map(booking => (
                    <div key={booking.id} className="booking-card">
                        <div className="booking-card-header">
                            <h3>{booking.doctorName}</h3>
                            <p>{booking.doctorSpecialty}</p>
                        </div>
                        <div className="booking-card-body">
                            <p><strong>Data:</strong> {format(new Date(booking.appointmentStart), 'd MMMM yyyy', { locale: pl })}</p>
                            <p><strong>Godzina:</strong> {format(new Date(booking.appointmentStart), 'HH:mm')}</p>
                            {booking.notes && <p className="notes"><strong>Cel wizyty:</strong> {booking.notes}</p>}
                        </div>
                        <div className="booking-card-actions">
                            <button className="action-button edit-button">Zmień Termin</button>
                            <button onClick={() => handleCancelBooking(booking.id)} className="action-button cancel-button">Anuluj Wizytę</button>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
};

export default MyBookingsPage;