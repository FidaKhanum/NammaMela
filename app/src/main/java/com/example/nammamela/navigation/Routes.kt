package com.example.nammamela.navigation


sealed class Routes(val route: String) {
    object Splash : Routes("splash")
    object Login : Routes("login")
    object Signup : Routes("signup")
    object Home : Routes("home")
    object Seat : Routes("seat")
    object Cast : Routes("cast")
    object FanWall : Routes("fanwall")
    object Admin : Routes("admin")
    object Ticket : Routes("ticket/{seat}") {
        fun createRoute(seat: String) = "ticket/$seat"
    }
    object Bot : Routes("bot")
}