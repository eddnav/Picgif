Picgif
==
Picnic's home assignment. Powered by the Giphy API.

Architecture
--
The app is structured using an MVVM inspired approach and layered as follows:

* Remote Data Source: includes models of Giphy's API's objects, mapping utilities to convert them into the data layer and other remote related code.
* Data Interface: includes data representation models and repositories, interfaces directly with data sources. In the case of Picgif, just with a remote source.
* Presentation: includes view models, subscribes to the data interface layer.
* View: Android UI, subscribes to the presentation layer.

Why MVVM?
--
MVVM was chosen over MVP due to benefits with Android lifecycle management brought by the Android Architecture Components. Furthermore, MVVM does not require tightly coupling the presentation and view in a 1:1 relationship, and thus makes for a leaner codebase without the many interfaces and methods of MVP.


Renditions
--
The `fixed_width_still` renditions were chosen for the list due to them being a good size for mobile; however, with a small change on the remote mapper, we can change this to the `fixed_width_downsampled` or `fixed_width` without a problem (thanks to Glide supporting gifs), but using these comes with a high cpu usage price to pay.

For the detail view, the `original` renditions were chosen due to them consistently providing an mp4 format option. As for the format itself, mp4 was chosen due to its small size compared to the original gifs and webp formats, and comparable to the downsized versions which sometimes are not present for some objects. However, mp4 are videos and thus, more cpu intensive; but an acceptable compromise, as the detail view is focused in getting the user a fast and smooth animation viewing experience.

Third Party Libraries
--
* LiveData and ViewModel: facilitates the VM in MVVM by enabling a lifecycle aware presentation (ViewModel) and lifecycle aware approach to observing it (LiveData).
* Retrofit: facilitates interaction with HTTP APIs, reduces API related code to a neat interface. 
* Moshi: chosen over Gson as the JSON library for conversions due to its Kotlin support and better efficiency.
* Dagger: facilitates dependency injection, improves code modularity and clarity over dependency management.
* RxJava: facilitates background tasks such as network calls and other asynchronous work.
* Glide: provides automatic image memory and disk caching as well as support for gifs. Chosen over Picasso for its gif support and chosen over Fresco due to its smaller footprint and the fact that it leverages the standard `ImageView`.
* ExoPlayer: replacement to `MediaPlayer` for video, adds a bit to the app's overall size (~500kb), but provides more powerful capabilities and reduces the risk of device specific issues due to it being a library.