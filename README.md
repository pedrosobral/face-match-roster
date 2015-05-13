# **FaceMatch Roster** #
***
The FaceMatch Roster has the following requirement:

 - Because we are using the Google Sign-In Button, you have to have:
	 - An emulator with an AVD that runs the Google APIs platform based on Android 4.2.2 or
	 - A physical device with Google Play services.

Before running the application make sure you have:

 - Internet connection, because our app relies on the Internet, and without that it will crash.
 - At least ONE google account logged in the physical device or emulator. It will provide a better flow when first use the app, and because we are having an annoying issue when the user tries to sign-in the first time, and doesn't have any google account on the device.

## How to test ##

### Setup ###

The best approach to test our application out, is to have at least two devices. It can be two emulators, one emulator and one physical device, etc.

 - In one device, sign-in as an Instructor
 - In the other devices, sign-in as a Student:
	 - When you go to sign-in as a Student in an emulator, make sure you have one photo to use in the sign-in process, or that the camera is enabled and working.

*Credentials*

If you would like, you can use our emails to sign-in:
 - Instructor:	facematchprofessor@gmail.com : facematch123
 - Student:		facematchstudent@gmail.com   : facematch123


### Instructor Version ###
Once signed-in as an Instructor, you will see the following screen:

![Main Screen Instructor Version](https://s3-us-west-2.amazonaws.com/allschools/instructor_main.png)

The purpose of this screen, is to:

 - List all classes that an instructor has already added;
 - Be able to add a class using the float button.

### Student Version ###
Once signed-in as an Instructor, you will see the following screen:

![Main Screen Student Version](https://s3-us-west-2.amazonaws.com/allschools/student_main.png)

The purpose of this screen, is to:

 - List all classes available (i.e. classes added by instructors);
 - Be able to enroll classes.

### Step-by-Step Video Demo ###

We provide a step-by-step video demo: [link video here](https://www.dropbox.com/s/wo57ostzc8rljmc/demo.mov?dl=0)

***
### Libraries ###

* [Butter Knife](https://github.com/JakeWharton/butterknife)
* [Picasso](https://github.com/square/picasso)
* [CircleImageView](https://github.com/hdodenhof/CircleImageView)
* [Material Dialogs](https://github.com/afollestad/material-dialogs)
* [Material Design Android Library](https://github.com/navasmdc/MaterialDesignLibrary)
* [FloatingActionButton](https://github.com/futuresimple/android-floating-action-button)
* [ActiveAndroid](https://github.com/pardom/ActiveAndroid)