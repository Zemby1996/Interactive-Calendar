// src/components/AuthModal.js
import React, { useState, useContext, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { loginUser, registerUser } from '../api/apiService';
import AuthContext from '../context/AuthContext';
import { toast } from 'react-toastify';

const AuthModal = ({ isOpen, onClose, initialMode = 'login' }) => {
    const [mode, setMode] = useState(initialMode);
    const [formData, setFormData] = useState({ email: '', password: '', firstName: '', lastName: '' });
    const { login } = useContext(AuthContext);
    const navigate = useNavigate();

    // Resetowanie trybu przy otwieraniu modala
    useEffect(() => {
        if (isOpen) {
            setMode(initialMode);
            setFormData({ email: '', password: '', firstName: '', lastName: '' }); // Czyścimy formularz
        }
    }, [isOpen, initialMode]);


    if (!isOpen) return null;

    const handleInputChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            if (mode === 'login') {
                const response = await loginUser({ email: formData.email, password: formData.password });
                login(response.data.data.token);
                toast.success("Zalogowano pomyślnie!");
                onClose();
                navigate('/');
            } else {
                // Upewniamy się, że wysyłamy wszystkie dane
                await registerUser(formData);
                toast.success('Rejestracja pomyślna! Możesz się teraz zalogować.');
                setMode('login');
            }
        } catch (err) {
            const errorMessage = err.response?.data?.message || `Wystąpił błąd.`;
            toast.error(errorMessage);
        }
    };

    const switchMode = () => {
        setMode(prevMode => prevMode === 'login' ? 'register' : 'login');
    };

    return (
        <div className="modal-overlay">
            <div className="modal-content">
                <button onClick={onClose} className="modal-close-button">&times;</button>
                <h2>{mode === 'login' ? 'Logowanie' : 'Rejestracja'}</h2>
                <form onSubmit={handleSubmit}>
                    
                    {/* --- TO JEST KLUCZOWY FRAGMENT, KTÓREGO PRAWDOPODOBNIE BRAKOWAŁO --- */}
                    {mode === 'register' && (
                        <>
                            <div className="form-group">
                                <label>Imię</label>
                                <input type="text" name="firstName" onChange={handleInputChange} required />
                            </div>
                            <div className="form-group">
                                <label>Nazwisko</label>
                                <input type="text" name="lastName" onChange={handleInputChange} required />
                            </div>
                        </>
                    )}
                    {/* --- KONIEC KLUCZOWEGO FRAGMENTU --- */}

                    <div className="form-group"><label>Email</label><input type="email" name="email" onChange={handleInputChange} required /></div>
                    <div className="form-group"><label>Hasło</label><input type="password" name="password" onChange={handleInputChange} required /></div>
                    
                    <button type="submit" className="modal-book-button">
                        {mode === 'login' ? 'Zaloguj' : 'Zarejestruj'}
                    </button>
                </form>
                <button onClick={switchMode} className="switch-mode-button">
                    {mode === 'login' ? 'Nie masz konta? Zarejestruj się' : 'Masz już konto? Zaloguj się'}
                </button>
            </div>
        </div>
    );
};

export default AuthModal;