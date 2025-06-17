// src/pages/AdminDashboardPage.js
import React, { useState, useEffect } from 'react';
import { getAllAppointments, deleteAppointment } from '../api/apiService';
import { format } from 'date-fns';
import { pl } from 'date-fns/locale';
import './AdminDashboardPage.css';
import Spinner from '../components/Spinner';

const AdminDashboardPage = () => {
    const [bookings, setBookings] = useState([]);
    const [isLoading, setIsLoading] = useState(true);

    const fetchAllBookings = async () => {
        try {
            const response = await getAllAppointments();
            setBookings(response.data.data);
        } catch (error) {
            console.error("Błąd podczas pobierania wszystkich wizyt:", error);
        } finally {
            setIsLoading(false);
        }
    };

    useEffect(() => {
        fetchAllBookings();
    }, []);

    const handleCancelBooking = async (appointmentId) => {
        if (window.confirm('Czy na pewno chcesz anulować tę wizytę? Ta akcja jest nieodwracalna.')) {
            try {
                await deleteAppointment(appointmentId);

                fetchAllBookings();
                alert('Wizyta została pomyślnie anulowana.');
            } catch (err) {
                alert("Nie udało się anulować wizyty.");
            }
        }
    };

    if (isLoading) {
        return <Spinner />;
    }





    return (
        <div className="admin-dashboard">
            <h1>Panel Administratora - Wszystkie Wizyty</h1>
            <table className="bookings-table">
                <thead>
                    <tr>
                        <th>Pacjent</th>
                        <th>Lekarz</th>
                        <th>Data i Godzina</th>
                        <th>Cel Wizyty</th>
                        <th>Akcje</th>
                    </tr>
                </thead>
                <tbody>
                    {bookings.map(booking => (
                        <tr key={booking.id}>
                            <td>{booking.patientFirstName} {booking.patientLastName}</td>
                            <td>{booking.doctorName}</td>
                            <td>{format(new Date(booking.appointmentStart), 'd MMMM yyyy, HH:mm', { locale: pl })}</td>
                            <td>{booking.notes || 'Brak'}</td>
                            <td>
                                <button className="action-button cancel-button" onClick={() => handleCancelBooking(booking.id)}>
                                    Anuluj
                                </button>
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
};

export default AdminDashboardPage;