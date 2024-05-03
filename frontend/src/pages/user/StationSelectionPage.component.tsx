import { useNavigate, useParams } from "react-router-dom";
import { Card } from "../../components/Card.tsx";
import { EventContext } from "../../providers/EventContext.tsx";
import { AppContext } from "../../providers/AppContext.tsx";
import { useContext, useState } from "react";
import { PiReceiptDuotone, PiChargingStationDuotone } from "react-icons/pi";
import { useStationSelectionPage } from "./StationSelectionPage.hooks.ts";


export const StationSelectionPage: React.FC = () => {
    const { eventId } = useContext(EventContext);
    const navigate = useNavigate();
    const appContext = useContext(AppContext);
    const {stationList, isLoading} = useStationSelectionPage();
    
    return (
        <div >
            <div className="flex flex-wrap justify-center">
                <Card
                    onClick={() => {
                        navigate(`/${eventId}/order`);
                    }}
                    title="Bestellung"
                    iconType={<PiReceiptDuotone />}
                />
                {isLoading ? (
                    <p>Loading...</p> 
                ) : (
                    stationList.map((station) => ( 
                        <Card
                            key={station.id} 
                            onClick={() => {
                                navigate(`/${eventId}/station/${station.id}`);
                            }}
                            title={station.name} 
                            iconType={<PiChargingStationDuotone />} 
                        />
                    ))
                )}
            </div>
        </div>
    );
}