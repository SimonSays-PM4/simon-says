import { BrowserRouter, Route, Routes } from "react-router-dom";
import { HomePage } from "./pages/HomePage";
import { AppProvider } from "./providers/AppProvider";
import { LoginPage } from "./pages/LoginPage";
import { EventListPageComponent } from "./pages/event/EventListPage.component.tsx";
import { Error404Page } from "./pages/Error404Page";
import { AuthorizedRoute } from "./routing/AuthorizedRoute";
import { EventCreatePageComponent } from "./pages/event/EventCreatePage.component.tsx";
import { IngredientListPageComponent } from "./pages/ingredient/IngredientListPage.component.tsx";
import { IngredientCreatePageComponent } from "./pages/ingredient/IngredientCreatePage.component.tsx";
import { AuthorizedEventRoute } from "./routing/AuthorizedEventRoute.tsx";
import { MenuListPageComponent } from "./pages/event/menu/MenuListPage.component.tsx";
import { MenuCreatePageComponent } from "./pages/event/menu/MenuCreatePage.component.tsx";
import { OrderCreatePageComponent } from "./pages/order/OrderCreatePage.component.tsx";
import { OrderListPageComponent } from "./pages/order/OrderListPage.component.tsx";
import { MenuItemPage } from "./pages/event/menu/MenuItemPage.component.tsx";
import { MenuItemEditPage } from "./pages/event/menu/MenuItemEditPage.component.tsx";
import { JoinPage } from "./pages/user/JoinPage.tsx";
import { StationSelectionPage } from "./pages/user/StationSelectionPage.component.tsx";
import { StationListPageComponent } from "./pages/station/StationListPage.component.tsx";
import { StationEditPageComponent } from "./pages/station/StationEditPage.component.tsx";
import { StationViewComponent } from "./pages/user/StationView.component.tsx";

export default function App() {
    return (
        <AppProvider>
            <BrowserRouter>
                <Routes>
                    <Route path="/login" element={<LoginPage />} />
                    <Route path="/:eventId/join" element={<JoinPage />} />
                    <Route path="/:eventId" element={<AuthorizedEventRoute><StationSelectionPage /></AuthorizedEventRoute>} />
                    <Route path="/" element={<AuthorizedRoute><HomePage /></AuthorizedRoute>} />
                    <Route path="/admin">
                        <Route path="/admin/events" element={<AuthorizedRoute><EventListPageComponent /></AuthorizedRoute>} />
                        <Route path="/admin/event/create" element={<AuthorizedEventRoute><EventCreatePageComponent /></AuthorizedEventRoute>}>
                            <Route path="/admin/event/create/:eventId" element={<AuthorizedEventRoute><EventCreatePageComponent /></AuthorizedEventRoute>} />
                        </Route>

                        <Route path="/admin/:eventId">
                            <Route path="/admin/:eventId/ingredients" element={<AuthorizedEventRoute><IngredientListPageComponent /></AuthorizedEventRoute>} />
                            <Route path="/admin/:eventId/menuItem" element={<AuthorizedEventRoute><MenuItemPage /></AuthorizedEventRoute>} />
                            <Route path="/admin/:eventId/menuItem/create" element={<AuthorizedEventRoute><MenuItemEditPage /></AuthorizedEventRoute>}>
                                <Route path="/admin/:eventId/menuItem/create/:menuItemId" element={<AuthorizedEventRoute><MenuItemEditPage /></AuthorizedEventRoute>} />
                            </Route>
                            <Route path="/admin/:eventId/ingredient/create" element={<AuthorizedEventRoute><IngredientCreatePageComponent /></AuthorizedEventRoute>}>
                                <Route path="/admin/:eventId/ingredient/create/:id" element={<AuthorizedEventRoute><IngredientCreatePageComponent /></AuthorizedEventRoute>} />
                            </Route>
                            <Route path="/admin/:eventId/menu" element={<AuthorizedEventRoute><MenuListPageComponent /></AuthorizedEventRoute>} />
                            <Route path="/admin/:eventId/menu/create" element={<AuthorizedEventRoute><MenuCreatePageComponent /></AuthorizedEventRoute>}>
                                <Route path="/admin/:eventId/menu/create/:menuId" element={<AuthorizedEventRoute><MenuCreatePageComponent /></AuthorizedEventRoute>} />
                            </Route>
                            <Route path="/admin/:eventId/station" element={<AuthorizedEventRoute><StationListPageComponent /></AuthorizedEventRoute>} />
                            <Route path="/admin/:eventId/station/create" element={<AuthorizedEventRoute><StationEditPageComponent /></AuthorizedEventRoute>}>
                                <Route path="/admin/:eventId/station/create/:stationId" element={<AuthorizedEventRoute><StationEditPageComponent /></AuthorizedEventRoute>} />
                            </Route>
                        </Route>
                    </Route>
                    <Route path="/:eventId/order">
                        <Route path="/:eventId/order" element={<AuthorizedEventRoute><OrderListPageComponent /></AuthorizedEventRoute>}></Route>
                        <Route path="/:eventId/order/create" element={<AuthorizedEventRoute><OrderCreatePageComponent /></AuthorizedEventRoute>}>
                            <Route path="/:eventId/order/create/:orderId" element={<AuthorizedEventRoute><OrderCreatePageComponent /></AuthorizedEventRoute>} />
                        </Route>
                    </Route>
                    <Route path="/:eventId/station/:stationId" element={<AuthorizedEventRoute><StationViewComponent /></AuthorizedEventRoute>} />

                    <Route path="*" element={<Error404Page />} />
                </Routes>
            </BrowserRouter>
        </AppProvider>
    );
}
