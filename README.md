# WalkingGroupApp
## Walking Group: Iteration 1 User Stories

1. Craete Account, Log-in, Log-out,
------------------------------------------
- As a new user, I want to be able to launch the app and signs up using my name, email, and enter a password.
    (Email addresses are used to uniquely identify accounts.)
- As a user, after logging in, if I close the app and relaunch it then the app remembers my credentials and automatically logs me in again. 
	(Suggestion: just store the token returned to you by the server).
- As a user, I can log out to have the app forget my login credentials so that the next time I launch it I am asked for my email address and password again.


2. Manage Account Monitoring
------------------------------------------
- As a user (such as a parent), I am able "monitor" the account of another user (such as my child).
- As a user, I am able to have 0 or more users monitoring me, and I may monitor 0 or more other users.
- As a user, I can list the users whom I monitor, and who monitors me.
    App must show me at least the name and email of the other account.
- As a user who monitors others, I can remove anyone from the list of users whom I monitor.
- As a user who is monitored by another user, I can remove anyone from the list of users who monitor me.
- As a user, I can add another user to the set of users I monitor (such as when monitoring my child).
    The child account must already exist and is identified by its email address.
- As a user, I can add another user to the set of users who monitor me (such as adding my parent).
    The parent account must already exist and is identified by its email address.


3. Create, View, and Join a Walking Group
------------------------------------------
- As a user, I can see on a map, centred on my GPS coordinates, locations (like schools) to which nearby walking groups around walk.
- As a user, I can create a new walking group by specifying the location (like a school) to which we will be walking, and one meeting place for the group. When creating it, I give the group a descriptive name.
- As a user, I can select an existing walking group from the map and join it.
- As a user, I can have any user I monitor (such as my child) join a walking group.
- As a user, I can leave any group I am part of, or remove a user I monitor (my child) from the group.
     (A user may be a member of any number of groups.)
- As a group leader, I can remove any user in my group from the group.

	 

General Notes:
- UI must be in a separate package from the data "Model".
- Use a singleton pattern to allow all parts of the UI to access the model.
- From any screen, the Android back button must do the "reasonable" thing.
- Strings shown to the user must be in string.xml
