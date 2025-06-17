
import React from 'react';
import BookingCalendar from '../components/BookingCalendar';

const HomePage = () => {
    return (
        <div>
            <h2>Zaplanuj swoją wizytę</h2>
            <p>Wybierz lekarza i dogodny termin z kalendarza poniżej.</p>
            <BookingCalendar />
        </div>
    );
};

export default HomePage;