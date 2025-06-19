// src/components/EditBookingModal.js
import React, { useState, useEffect } from 'react';
import './EditBookingModal.css'; // Upewnij się, że ten plik CSS istnieje

const EditBookingModal = ({ isOpen, onClose, bookingToEdit, onUpdate }) => {
    const [notes, setNotes] = useState('');

    useEffect(() => {
        if (bookingToEdit) {
            setNotes(bookingToEdit.notes || '');
        }
    }, [bookingToEdit]);

    if (!isOpen || !bookingToEdit) {
        return null;
    }

    const handleSubmit = (e) => {
        e.preventDefault();
        onUpdate(notes);
    };

    return (
        <div className="modal-overlay">
            <div className="modal-content">
                <button onClick={onClose} className="modal-close-button">&times;</button>
                <h2>Edytuj Cel Wizyty</h2>
                <p>
                    <strong>Lekarz:</strong> {bookingToEdit.doctorName}<br />
                    <strong>Data:</strong> {new Date(bookingToEdit.appointmentStart).toLocaleString('pl-PL')}
                </p>
                <form onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label htmlFor="edit-notes">Cel wizyty / Notatki:</label>
                        <textarea
                            id="edit-notes"
                            value={notes}
                            onChange={(e) => setNotes(e.target.value)}
                            rows="4"
                        ></textarea>
                    </div>
                    <button type="submit" className="modal-book-button">Zapisz zmiany</button>
                </form>
            </div>
        </div>
    );
};

export default EditBookingModal;