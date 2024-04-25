package ch.zhaw.pm4.simonsays.exception

class AssemblyStationAlreadyDefinedException(message: String = "An assembly station is already defined for this event") : RuntimeException(message)  {}