import {useCallback, useContext, useEffect, useState} from "react";
import {getStationService} from "../../api.ts";
import {AppContext} from "../../providers/AppContext.tsx";
import {EventContext} from "../../providers/EventContext.tsx";
import {StationDTO} from "../../gen/api/index.ts";
import {NotificationType} from "../../enums/NotificationType.ts";

type StationSelectionProps = {
    stationList: StationDTO[],
    isLoading: boolean
}

export const useStationSelectionPage = (): StationSelectionProps => {
    const { eventId } = useContext(EventContext);
    const appContext = useContext(AppContext);
    const [isLoading, setIsLoading] = useState(false);
    const [stationList, setStationList] = useState<StationDTO[]>([]);

    const { loginInfo } = useContext(AppContext);
    const stationService = getStationService(loginInfo.userName, loginInfo.password);

    useEffect(() => {
        reloadStations()
    }, []);

    const reloadStations = useCallback(() => {
        setIsLoading(true);
        stationService.getStations(eventId).then((response) => {
            setStationList(response.data);
        }).catch(()=> {
            appContext.addNotification(NotificationType.ERR, "Stationen konnten nicht geladen werden");
        }).finally(()=> {
            setIsLoading(false);
        })
    }, [eventId])

    return {stationList, isLoading}
}