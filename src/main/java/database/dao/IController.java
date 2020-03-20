package database.dao;

public interface IController {

    // PLAYGROUND, playgroundName must be unique!
    // getAllPlaygrounds()
    // createPlayground(activeUser, playground)
    // assignUserToPlayground(activeUser, playgroundName, userID) // only for pædagoger
    // updateUserInPlayground(activeUser, playgroundName, updatedUser)
    // retractUserFromPlayground(activeUser, playgroundName, userID)

    // PLAYGROUND EVENTS
    // getEventsInPlayground(playgroundName) // differentiate between upcoming and past events?
    // addEventToPlayground(activeUser, playgroundName, eventToBeAdded)
    // updateEventInPlayground(activeUser, playgroundName, updatedEvent)
    // deleteEventInPlayground(activeUser, playgroundName, eventID)
    // signupForEventInPlaygound(playgroundName, userID)


    // PLAYGROUND MESSAGES
    // getPlaygroundMessage(playgroundName) // maybe Date as 2nd argument?
    // createPlaygroundMessage(activeUser, playgroundName)
    // updatePlaygroundMessage(activeUser, playgroundName)
    // deletePlaygroundMessage(activeUser, playgroundName)


    // USER, username/email is unique?
    // createUser(activeUser, userToBeCreated) // for admin to setup users with privileges
    // updateUser(activeUser, updatedUser) // do we allow updates on all fields? switch case ladder maybe?
    // deleteUser(activeUser, userID)
    // getUser(activeUser, ...userID) // is information sentitive?
}
