import { BrowserRouter, Route, Routes } from "react-router-dom";
import { HomePage } from "./pages/HomePage";
import { AppProvider } from "./providers/AppProvider";
import { ActivePageType } from "./enums/ActivePageType";
import { LoginPage } from "./pages/LoginPage";
import { EventListPage } from "./pages/event/EventListPage";
import { Error404Page } from "./pages/Error404Page";
import { AuthorizedRoute } from "./routing/AuthorizedRoute";
import { EventCreatePageComponent } from "./pages/event/EventCreatePage.component.tsx";

export default function App() {
    return (
        <AppProvider>
            <BrowserRouter>
                <Routes>
                    <Route path="/login" element={<LoginPage />} />
                    <Route path="/" element={<AuthorizedRoute activePageType={ActivePageType.Home}><HomePage /></AuthorizedRoute>} />
                    <Route path="/events" element={<AuthorizedRoute activePageType={ActivePageType.EventList}><EventListPage /></AuthorizedRoute>} />
                    <Route path="/event/create" element={<AuthorizedRoute activePageType={ActivePageType.Event}><EventCreatePageComponent /></AuthorizedRoute>}>
                        <Route path="/event/create/:id" element={<AuthorizedRoute activePageType={ActivePageType.Event}><EventCreatePageComponent /></AuthorizedRoute>}/>
                    </Route>

                    <Route path="*" element={<Error404Page />} />
                </Routes>
            </BrowserRouter>
        </AppProvider>
    );
}
