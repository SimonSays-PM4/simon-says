import { useCallback, useContext, useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { Control, FieldErrors, FieldValues, UseFormGetValues, UseFormHandleSubmit, UseFormRegister, useForm } from "react-hook-form";

import { nameof } from "ts-simple-nameof";
import {IngredientDTO, StationCreateUpdateDTO} from "../../gen/api";
import {ValueLabel} from "../../models/ValueLabel.ts";
import {AppContext} from "../../providers/AppContext.tsx";
import {EventContext} from "../../providers/EventContext.tsx";
import {getIngredientService, getStationService} from "../../api.ts";
import {NotificationType} from "../../enums/NotificationType.ts";

type StationActions = {
    deleteStation: () => void;
    saveStation: (stationToSave: FieldValues) => void;
    onFormInvalid: (fieldErros: FieldErrors<FieldValues>) => void;
};
type StationCreateReturnProps = {
    station: StationCreateUpdateDTO;
    ingredientOptions: ValueLabel[];
    selectedIngredientOptions: ValueLabel[];
    stationActions: StationActions;
    isLoading: boolean;
    isSaving: boolean;
    isAssembly: boolean;
    setIsAssembly: (is:boolean)=> void;

    formRegister: UseFormRegister<FieldValues>;
    handleSubmit: UseFormHandleSubmit<FieldValues, undefined>;
    formErrors: FieldErrors<FieldValues>;
    formGetValues: UseFormGetValues<FieldValues>;
    formControl: Control<FieldValues, any>;
};

export const useStationEditPage = (): StationCreateReturnProps => {
    const appContext = useContext(AppContext);
    const { eventId } = useContext(EventContext);

    const navigate = useNavigate();
    const { stationId: id } = useParams();
    const stationId = id ? Number(id) : 0;

    const [station, setStation] = useState<StationCreateUpdateDTO>({ id: 0, name: "", ingredients: [],assemblyStation:false });
    const [ingredientOptions, setIngredientOptions] = useState<ValueLabel[]>([]);
    const [selectedIngredientOptions, setSelectedIngredientOptions] = useState<ValueLabel[]>([]);
    const [isLoading, setIsLoading] = useState<boolean>(true);
    const [isSaving, setIsSaving] = useState<boolean>(false);
    const [ingredients, setIngredients] = useState<IngredientDTO[]>([]);
    const [isAssembly, setIsAssembly] = useState(false);
    const { loginInfo } = useContext(AppContext);
    const stationService = getStationService(loginInfo.userName, loginInfo.password);
    const ingredientService = getIngredientService(loginInfo.userName, loginInfo.password);

    const {
        register,
        handleSubmit,
        formState: { errors },
        getValues,
        setValue,
        control
    } = useForm();

    const loadIngredients = useCallback(async () => {
        try {
            const items = await ingredientService.listIngredients(eventId);
            const availableStationItems = items.data;
            setIngredients(availableStationItems);
            setIngredientOptions([...availableStationItems.map((item) => { return { value: item.id, label: item.name } })]);

            register(nameof<StationCreateUpdateDTO>(e => e.ingredients), { value: [] });

            if (stationId && stationId > 0) {
                const response = await stationService.getStation(eventId, stationId);
                const receivedStation = response.data as StationCreateUpdateDTO;
                setStation(receivedStation);
                setIsAssembly(receivedStation.assemblyStation)

                const receivedStationItems = receivedStation.ingredients.map((item: IngredientDTO) => { return { value: item.id, label: item.name } });
                setSelectedIngredientOptions(Array.from(receivedStationItems));
                setValue(nameof<StationCreateUpdateDTO>(e => e.ingredients), Array.from(receivedStationItems));
            }
        }
        catch (_) {
            appContext.addNotification(NotificationType.ERR, "Beim Laden des Stations ist ein Fehler aufgetreten.");
        }
    }, []);

    useEffect(() => {
        setIsLoading(() => true);
        loadIngredients()
            .then(() => setIsLoading(() => false));
    }, [stationId, eventId]);

    const onFormInvalid = (data: FieldValues) => {
        const formStationItems = data.stationItems as ValueLabel[];
        const stationToSave = data as StationCreateUpdateDTO;

        stationToSave.id = stationId > 0 ? stationId : 0;
        stationToSave.ingredients = ingredients.filter((item) => formStationItems.some((selected) => selected.value === item.id));

        setStation(stationToSave);
    };

    const saveStation = (data: FieldValues) => {

        const formStationItems = data.ingredients as ValueLabel[];
        const stationToSave = data as StationCreateUpdateDTO;

        stationToSave.id = stationId > 0 ? stationId : undefined;
        stationToSave.ingredients = ingredients.filter((item) => formStationItems.some((selected) => selected.value === item.id));
        stationToSave.assemblyStation = isAssembly;
        setStation(stationToSave);

        stationService
            .putStation(eventId, stationToSave)
            .then((response) => {
                if (response.status === 201 || response.status === 200) {
                    navigate("../station");
                    appContext.addNotification(NotificationType.OK, `Station '${stationToSave.name}' wurde erfolgreich ${stationId > 0 ? "bearbeitet" : "erstellt"}.`);
                } else {
                    appContext.addNotification(NotificationType.ERR, `Beim ${stationId > 0 ? "Speichern" : "Erstellen"} des Stations ist ein Fehler aufgetreten.`);
                }
            })
            .catch((e) => {
                if (e.response?.data?.message == "An assembly station is already defined for this event") {
                    appContext.addNotification(NotificationType.ERR, `Es existiert schon ein Assembly Station`);
                } else {
                    appContext.addNotification(NotificationType.ERR, `Beim ${stationId > 0 ? "Speichern" : "Erstellen"} des Stations ist ein Fehler aufgetreten.`);
                }

            })
            .finally(() => {
                setIsSaving(false);
            });
    };

    const deleteStation = useCallback(() => {
        if (stationId > 0) {
            setIsSaving(true);

            stationService.deleteStation(stationId, eventId)
                .then(() => {
                    navigate("/station");
                    appContext.addNotification(NotificationType.OK, `Station wurde erfolgreich gelöscht.`);
                })
                .catch(() => {
                    appContext.addNotification(NotificationType.ERR, `Beim Löschen des Stations ist ein Fehler aufgetreten.`);
                })
                .finally(() => {
                    setIsSaving(false);
                });
        }
    }, [id]);

    const stationActions: StationActions = {
        saveStation,
        deleteStation,
        onFormInvalid
    };

    return { station, selectedIngredientOptions: selectedIngredientOptions, ingredientOptions: ingredientOptions, stationActions, isLoading, isSaving, formErrors: errors, formGetValues: getValues, formRegister: register, formControl: control, handleSubmit, isAssembly, setIsAssembly };
};
