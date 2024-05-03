import { useNavigate } from "react-router-dom";
import { Card } from "../../components/Card.tsx";
import { EventContext } from "../../providers/EventContext.tsx";
import { useContext } from "react";
import { PiReceiptDuotone, PiChargingStationDuotone } from "react-icons/pi";
import { useStationSelectionPage } from "./StationSelectionPage.hooks.ts";


export const StationSelectionPage: React.FC = () => {
    const { eventId } = useContext(EventContext);
    const navigate = useNavigate();
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