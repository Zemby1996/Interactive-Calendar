// src/components/ProtectedRoute.js
import React, { useContext } from 'react';
import { Navigate, Outlet } from 'react-router-dom';
import AuthContext from '../context/AuthContext';

// Ten komponent będzie sprawdzał, czy użytkownik jest adminem
const ProtectedRoute = () => {
    const { auth } = useContext(AuthContext);

    // Sprawdzamy, czy użytkownik jest zalogowany i czy jego rola to ROLE_ADMIN
    // Uwaga: W tokenie JWT rola może być w polu 'authorities' lub 'roles' w zależności od konfiguracji backendu
    // My zakładamy, że jest w 'role' po zdekodowaniu.
    const isAdmin = auth.token && auth.user?.role === 'ROLE_ADMIN';

    if (!auth.token) {
        // Jeśli użytkownik nie jest w ogóle zalogowany, możemy go przekierować na stronę główną
        return <Navigate to="/" replace />;
    }
    
    if (!isAdmin) {
        // Jeśli jest zalogowany, ale nie jest adminem, też go odsyłamy.
        // Można by tu wyświetlić stronę "Brak dostępu".
        console.warn("Próba dostępu do panelu admina przez użytkownika bez uprawnień.");
        return <Navigate to="/" replace />;
    }

    // Jeśli wszystkie warunki są spełnione, renderujemy właściwą stronę (np. panel admina)
    return <Outlet />;
};

export default ProtectedRoute;