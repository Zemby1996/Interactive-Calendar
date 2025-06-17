// src/components/Navbar.js
import React, { useContext } from 'react';
import { Link, useNavigate } from 'react-router-dom'; // 1. Importujemy useNavigate
import AuthContext from '../context/AuthContext';
import './Navbar.css';

const Navbar = ({ onLoginClick, onRegisterClick }) => {
    const { auth, logout } = useContext(AuthContext);
    const navigate = useNavigate(); // 2. Tworzymy funkcję do nawigacji

    const handleLogout = () => {
        logout(); // 3. Najpierw wykonujemy logikę wylogowania z kontekstu
        navigate('/'); // 4. Następnie programowo przekierowujemy na stronę główną
    };

    return (
        <nav className="navbar">
            <div className="navbar-brand">
                <Link to="/">Terminarz wizyt</Link>
            </div>
            <div className="navbar-auth">
                {auth.token ? (
                    <>
                        <span className="navbar-user">Witaj, {auth.user?.sub}</span>
                        <Link to="/moje-wizyty" className="navbar-link">Moje Wizyty</Link>
                        {/* W przyszłości można tu dodać warunek, aby ten link widział tylko admin */}
                        <Link to="/panel-admina" className="navbar-link">Panel Admina</Link>
                        
                        {/* 5. Używamy naszej nowej funkcji */}
                        <button onClick={handleLogout} className="navbar-button">Wyloguj</button>
                    </>
                ) : (
                    <>
                        <button onClick={onLoginClick} className="navbar-button">Zaloguj</button>
                        <button onClick={onRegisterClick} className="navbar-button secondary">Zarejestruj</button>
                    </>
                )}
            </div>
        </nav>
    );
};

export default Navbar;