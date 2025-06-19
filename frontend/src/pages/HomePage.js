import React from 'react';
import BookingCalendar from '../components/BookingCalendar';

const HomePage = () => {
    return (
        <div className="max-w-4xl mx-auto p-6">
            <h2 className="text-3xl font-bold mb-4">Zaplanuj swoją wizytę</h2>
            <p className="text-gray-700 mb-6">Wybierz lekarza i dogodny termin z kalendarza poniżej.</p>
            <BookingCalendar />
        </div>
    );
};

export default HomePage;
