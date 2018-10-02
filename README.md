# bacticount
Quantify bacteria with LAMP reactions - proof of concept Android app accompanying EBioMedicine paper

Details, downloads, and more available at httpbacticount.com

Read the paper here: https://www.ebiomedicine.com/article/S2352-3964(18)30356-6/fulltext

## FAQ

_Why don't I see any releases yet?_

This project started several years ago and was imported from Bitbucket. What you see is what's published to our website.

_Why doesn't the app work with my phone?_

In the activity RecordingReaction, a series of steps to manage white balance, autofocus, and idiosynchrasies of the Galaxy S7 lens and sensors happens to ensure that... the phone does not try to apply any post processing to the image, that the exposure settings remains exactly the same throughout the recording, and that the focus keeps the targeted pixel areas exactly where the fluorescence vials are for the particular experimental setup.  We take photos just under the raw quality limit and measure pixel brightness very carefully.  Because each manufacturer has different sensors, lenses, photo dimensions, and software controls, we only were able to complete the project by sticking with a single reference device.  With some effor, another individual could calibrate or create calibration options for a different phone model.

_Why isn't the app on Google Play?_

Update! It is now... download here:

https://play.google.com/store/apps/details?id=com.garynfox.pathogenanalyzer
