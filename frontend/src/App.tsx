import { BrowserRouter, Route, Routes } from "react-router-dom";
import { HomePage } from "./pages/HomePage";
import { DevelopmentPage } from "./pages/DevelopmentPage";
import { MainLayout } from "./layouts/MainLayout";
import { AppProvider } from "./providers/AppProvider";
import { ActivePageType } from "./enums/ActivePageType";
import { LoginPage } from "./pages/LoginPage";
import { EventListPage } from "./pages/event/EventListPage";
import { EventPage } from "./pages/event/EventPage";

export default function App() {
    return (
        <AppProvider>
            <BrowserRouter>
                <Routes>
                    <Route path="/login" element={<LoginPage />} />
                    <Route path="/" element={<MainLayout activePageType={ActivePageType.Home}><HomePage /></MainLayout>} />
                    <Route path="/events" element={<MainLayout activePageType={ActivePageType.EventList}><EventListPage /></MainLayout>} />
                    <Route path="/event/:id" element={<MainLayout activePageType={ActivePageType.Event}><EventPage /></MainLayout>} />
                    <Route path="/dev" element={<MainLayout activePageType={ActivePageType.Development}><DevelopmentPage /></MainLayout>} />
                </Routes>
            </BrowserRouter>
        </AppProvider>
    );
}
