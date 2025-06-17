// src/components/BookingModal.js
import React, { useState, useContext } from 'react';
import AuthContext from '../context/AuthContext';
import { format } from 'date-fns'; // Biblioteka do formatowania dat

const BookingModal = ({ isOpen, onClose, selectedSlot, onBook }) => {
    const [notes, setNotes] = useState('');
    const { auth } = useContext(AuthContext); // Pobieramy informacje o zalogowanym użytkowniku

    if (!isOpen) {
        return null;
    }

    const handleSubmit = (e) => {
        e.preventDefault();
        onBook({ notes }); // Przekazujemy notatki do funkcji rezerwującej
        setNotes(''); // Czyścimy pole po wysłaniu
    };

    return (
        <div className="modal-overlay">
            <div className="modal-content">
                <button onClick={onClose} className="modal-close-button">&times;</button>
                <h2>Rezerwacja Wizyty</h2>
                {selectedSlot && (
                    <p>Wybrany termin: <strong>{format(selectedSlot.start, "d MMMM yyyy, HH:mm")}</strong></p>
                )}

                {auth.token ? (
                    // Widok dla zalogowanego użytkownika
                    <form onSubmit={handleSubmit}>
                        <div className="form-group">
                            <label htmlFor="notes">Cel wizyty (opcjonalnie):</label>
                            <textarea
                                id="notes"
                                value={notes}
                                onChange={(e) => setNotes(e.target.value)}
                                rows="4"
                            ></textarea>
                        </div>
                        <button type="submit" className="modal-book-button">Zarezerwuj</button>
                    </form>
                ) : (
                    // Widok dla gościa
                    <div>
                        <p><strong>Proszę się zalogować, aby zarezerwować termin.</strong></p>
                        {/* W przyszłości można tu dodać przycisk przekierowujący do logowania */}
                    </div>
                )}
            </div>
        </div>
    );
};

export default BookingModal;