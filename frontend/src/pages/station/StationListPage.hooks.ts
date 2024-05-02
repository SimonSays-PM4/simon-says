import {useCallback, useContext, useEffect, useState} from "react";
import {getStationService} from "../../api.ts";
import {AppContext} from "../../providers/AppContext.tsx";
import {EventContext} from "../../providers/EventContext.tsx";
import {StationDTO} from "../../gen/api";
import {NotificationType} from "../../enums/NotificationType.ts";

type StationActions = {
    deleteStation: () => void,
    setStationToDelete: (ingredient: StationDTO) => void,
    stationToDelete: StationDTO
}

type StationReturnProps = {
    stationList: StationDTO[],
    stationActions: StationActions,
    isLoading:boolean,
    showDeletePopup:boolean
    setShowDeletePopup:(set:boolean)=>void
}

export const useStationListPage = (): StationReturnProps => {
    const { eventId } = useContext(EventContext);
    const appContext = useContext(AppContext);

    const [stationToDelete, setStationToDelete] = useState<StationDTO>({ id: 0, name: "",assemblyStation:false, ingredients:[]});
    const [isLoading, setIsLoading] = useState(false);
    const [showDeletePopup, setShowDeletePopup] = useState(false);
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
            appContext.addNotification(NotificationType.ERR, "Failed to load Stations");
        }).finally(()=> {
            setIsLoading(false);
        })
    }, [eventId])

    const deleteStation = useCallback(() => {
        if (stationToDelete.id && stationToDelete.id > 0) {
            setIsLoading(true);
            stationService.deleteStation(eventId, stationToDelete.id).then(() => {
                setShowDeletePopup(false);
            }).catch(() => {
                appContext.addNotification(NotificationType.ERR, "Failed to delete station");
            }).finally(() => {
                setIsLoading(false);
            })
        } else {
            appContext.addNotification(NotificationType.ERR, "Cannot delete station with invalid event id");
        }
    }, [stationToDelete.id]);

    return {stationList, stationActions:{stationToDelete, setStationToDelete,deleteStation },isLoading,showDeletePopup,setShowDeletePopup}
}