This app was built as test project for Yandex school of mobile development. 
This is translator app which uses Yndex Translation API. It translates user input from/to more than 90 languages,
saves translation history to local storage and allow user to add translations to favorities. It is built in MVP style with
minimum of third party libraries. I use Retrofit to contact the API, Dagger2 to implement DI pattern and Butterknife to simplify code.
App consists from one activity and few fragments which dinamicaly sets into the activity container. Each fragment has it's presenter.
Fragment take care about communication with system and display data to the user, while presenter get this data from network or lical storage.
I used native android functionality to implement local storage, because it is very simple app and I wanted to practice my skills in this topic.
I wrote some tests for those parts of code which, I guess, sould be covered.
