
import React, { useState, useEffect, useRef, useContext, useCallback } from 'react';
import FullCalendar from '@fullcalendar/react';
import dayGridPlugin from '@fullcalendar/daygrid/index.js';
import timeGridPlugin from '@fullcalendar/timegrid/index.js';
import interactionPlugin from '@fullcalendar/interaction/index.js';
import BookingModal from './BookingModal';
import AuthContext from '../context/AuthContext';
import { toast } from 'react-toastify';
import Spinner from './Spinner';

import { getAllDoctors, getPublicAppointments, createAppointment } from '../api/apiService';

const BookingCalendar = () => {
    // Istniejące stany
    const [doctors, setDoctors] = useState([]);
    const [selectedDoctorId, setSelectedDoctorId] = useState('');
    const [events, setEvents] = useState([]);
    const calendarRef = useRef(null);
    const { auth } = useContext(AuthContext);

    // NOWE stany do obsługi modala
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [selectedSlot, setSelectedSlot] = useState(null);
    const [doctorsLoading, setDoctorsLoading] = useState(true);
    const [eventsLoading, setEventsLoading] = useState(false);

    // 1. Pobieranie listy lekarzy przy pierwszym załadowaniu komponentu
    useEffect(() => {
        const fetchDoctors = async () => {
            try {
                const response = await getAllDoctors();
                setDoctors(response.data.data);
                if (response.data.data.length > 0) {
                    setSelectedDoctorId(response.data.data[0].id);
                }
            } catch (error) {
                console.error("Błąd podczas pobierania lekarzy:", error);
            } finally {
                setDoctorsLoading(false); // Kończymy ładowanie lekarzy
            }
        };
        fetchDoctors();
    }, []);

    // 2. Pobieranie wizyt, gdy zmieni się wybrany lekarz lub zakres dat w kalendarzu
    const fetchAppointments = useCallback(async () => {
        if (!selectedDoctorId || !calendarRef.current) return;
        setEventsLoading(true);
        const calendarApi = calendarRef.current.getApi();
        const start = calendarApi.view.activeStart.toISOString();
        const end = calendarApi.view.activeEnd.toISOString();
        try {
            const response = await getPublicAppointments(selectedDoctorId, start, end);
            const formattedEvents = response.data.data.map(app => ({
                id: app.id,
                title: `🔒 Zajęty`,
                start: app.appointmentStart,
                end: app.appointmentEnd,
                classNames: ['busy-event']
            }));
            setEvents(formattedEvents);
        } catch (error) {
            console.error("Błąd podczas pobierania wizyt:", error);
        } finally {
            setEventsLoading(false);
        }
    }, [selectedDoctorId]);

    // Uruchom pobieranie wizyt, gdy zmieni się lekarz
    useEffect(() => {
        if (!doctorsLoading) { // Uruchom pobieranie wizyt dopiero po załadowaniu lekarzy
            fetchAppointments();
        }
    }, [ fetchAppointments, doctorsLoading]);


    // 3. Obsługa zdarzeń kalendarza
    const handleDoctorChange = (event) => {
        setSelectedDoctorId(event.target.value);
    };

    const handleDateClick = (arg) => {
        if (new Date(arg.date) < new Date()) {
            toast.warn("Nie można rezerwować wizyt w przeszłości.");
            return;
        }
        const endDate = new Date(arg.date.getTime() + 30 * 60000);
        setSelectedSlot({ start: arg.date, end: endDate });
        setIsModalOpen(true);
    };

    const handleDatesSet = (dateInfo) => {
        fetchAppointments();
    };

    const handleBookingSubmit = async ({ notes }) => {
        if (!auth.token) {
            toast.error("Musisz być zalogowany, aby dokonać rezerwacji.");
            return;
        }
        try {
            const appointmentData = {
                appointmentStart: selectedSlot.start.toISOString(),
                appointmentEnd: selectedSlot.end.toISOString(),
                doctorId: selectedDoctorId,
                notes: notes,
            };
            await createAppointment(appointmentData);
            toast.success("Wizyta została pomyślnie zarezerwowana!");
            setIsModalOpen(false); // Zamknij modal
            fetchAppointments(); // Odśwież wizyty w kalendarzu - KLUCZOWY MOMENT!
        } catch (error) {
            console.error("Błąd rezerwacji:", error);
            const errorMessage = error.response?.data?.message || "Wystąpił błąd.";
            toast.error(`Nie udało się zarezerwować wizyty: ${errorMessage}`);
        }
    };

    if (doctorsLoading) {
        return <Spinner />;
    }




    return (
        <div>
            <div className="doctor-selector">
                <label htmlFor="doctor">Wybierz lekarza: </label>
                <select id="doctor" value={selectedDoctorId} onChange={handleDoctorChange}>
                    <option value="" disabled>-- Wybierz --</option>
                    {doctors.map(doctor => (
                        <option key={doctor.id} value={doctor.id}>
                            {doctor.name} ({doctor.specialty})
                        </option>
                    ))}
                </select>
            </div>

            <FullCalendar
                ref={calendarRef}
                plugins={[dayGridPlugin, timeGridPlugin, interactionPlugin]}
                initialView="timeGridWeek"
                height="auto"
                allDaySlot={false}
                loading={eventsLoading}

                headerToolbar={{
                    left: 'prev,next today',
                    center: 'title',
                    right: 'dayGridMonth,timeGridWeek,timeGridDay'
                }}
                events={events} // Nasze wizyty z backendu
                dateClick={handleDateClick} // Co się dzieje po kliknięciu na kalendarz
                datesSet={handleDatesSet} // Co się dzieje po zmianie zakresu dat
                locale="pl" // Ustawiamy język polski
                buttonText={{
                    today: 'Dziś',
                    month: 'Miesiąc',
                    week: 'Tydzień',
                    day: 'Dzień',
                }}
                allDayText='Cały dzień'
                slotMinTime="08:00:00" // Ograniczenie godzin wyświetlania
                slotMaxTime="18:00:00"

                selectAllow={(selectInfo) => {
                    return new Date(selectInfo.start) > new Date(); // Pozwól na zaznaczenie tylko, jeśli data początkowa jest w przyszłości
                }}
            />
            <BookingModal
                isOpen={isModalOpen}
                onClose={() => setIsModalOpen(false)}
                selectedSlot={selectedSlot}
                onBook={handleBookingSubmit}
            />
        </div>
    );
};

export default BookingCalendar;