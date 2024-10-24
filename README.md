# SoundUp - Audio Processing Application

**SoundUp** is an advanced application for the management and processing of audio files, designed for tasks such as volume normalization, duplicate removal, and audio quality enhancement. The project is primarily developed in **Java** and leverages a series of specialized libraries for audio file manipulation.

## Project Description

SoundUp enables users to upload audio files, apply normalizations and enhancements, and visualize comparisons between the original and processed files. Additionally, the graphical interface simplifies user interaction with the advanced tools offered by the system. The application also includes a real-time audio visualizer that graphically represents the sound amplitude during playback.

### Key Features

- **Audio Normalization**: Increases the volume of audio files uniformly without reducing quality.
- **Sound Enhancements**: Improves bass, midrange, treble, and other sound characteristics.
- **Duplicate Removal**: Deletes audio files that have already been normalized or enhanced.
- **User-Friendly Graphical Interface**: Allows users to visualize comparisons between the original and processed files, along with a real-time graphical representation of audio through bars indicating the sound amplitude at various moments.
- **Gain Adjustment**: Enhances the brightness of the audio signal.
- **High-Frequency Filter**: Improves high-frequency sounds while minimizing low-frequency sounds.
- **Band-Pass Filter**: Allows frequencies within a specified range to pass and attenuates frequencies outside that range.
- **Volume Normalization**: Adjusts audio to ensure consistent volume levels.
- **Memory Management**: Checks available memory before applying filters to avoid memory errors.

---

---

## Media Showcase

To give a better understanding of **SoundUp** and its functionalities, here are some screenshots and videos demonstrating the interface, the audio visualizer, and the processing tools in action.

### Screenshots

#### Main Interface
![Main Interface](![image](https://github.com/user-attachments/assets/a6e27ede-a236-44f4-9da9-e5ab116e7b68)
)
![image](https://github.com/user-attachments/assets/68b82703-4200-47d4-b6d8-5e6ddb5fab99)

![image](https://github.com/user-attachments/assets/ff981e37-a654-45a7-b659-9ec7461c04a2)

![image](https://github.com/user-attachments/assets/13363a51-8382-4cce-9a18-93c167f1d045)

![image](https://github.com/user-attachments/assets/349f34dd-7932-465c-b25b-c650475dd8be)

_This screenshot shows the main interface, where users can upload and manage their audio files._

#### Sound Processing
Sound Processing
![image](![image](https://github.com/user-attachments/assets/03ccd927-a17c-41e2-b12f-1740e46a51cc))
_An example of the filters and enhancements applied to an audio file._


## Features and General Code Structure

### Common Features:

- **File Visualization**: Loads and displays both original and processed audio files, enabling users to perform direct comparisons. The `AudioVisualizer` component also draws real-time bars to represent the amplitude of the processed audio waves.
  
- **Path Handling**: Ensures the application has access to the correct paths for the audio files.

- **User Interaction**: Handles events such as minimizing or closing windows, and manages the deletion of temporary files to keep the system clean.

- **Animation and Visual Feedback**: Utilizes visual components, such as a loading circle (`LoadingCircle`), to indicate that processing is underway, enhancing the user experience. During playback, the `AudioVisualizer` updates the visual bars to represent the audio amplitudes.

- **Audio File Control**: Uses static variables, such as `totalAudioFiles`, which is an `AtomicInteger`, to safely track the total number of audio files being processed. This allows multiple execution threads to update this variable without concurrency issues.

### General Code Structure:

- **Constructors**: Initialize graphical interfaces and necessary components, setting up panels and animations accordingly. The `AudioVisualizer` is an extension of `JPanel`, configured to receive the audio file's bytes as input and draw amplitude bars in the graphical interface.

- **Processing Methods**: Include logic for cleaning file names and managing visualization. In the case of `AudioVisualizer`, the audio bytes are transformed into visual data that are rendered as vertical bars, whose heights represent the audio's amplitude.

- **Redraw and Update Methods**: Update the graphical interface in response to events. The `paintComponent` method in `AudioVisualizer` is responsible for drawing both the background and amplitude bars. Additionally, a `Timer` is used to update the bar heights (`BarHeights`) at regular intervals, allowing for a smooth and dynamic visualization of the audio.

---

## Contributions

Contributions are welcome. If you would like to contribute to the project, please follow these steps:

1. Fork the repository.
2. Create a new branch for your feature (`git checkout -b feature/AmazingFeature`).
3. Make the necessary changes.
4. Ensure the changes do not break the project (`./gradlew test`).
5. Submit a pull request.

### Contribution Requirements

- Follow the project's coding style and conventions.
- Maintain a clean and clear commit history.
- Document changes in the `CHANGELOG.md` file.

---

## Additional Resources

If you have any questions or issues, feel free to consult the following documentation and resources:

- [Java Documentation](https://docs.oracle.com/javase/8/docs/)
- [JAudioTagger Usage Guide](http://www.jthink.net/jaudiotagger/)
- [Swing Tutorials](https://docs.oracle.com/javase/tutorial/uiswing/)
- [Java Sound Documentation](https://docs.oracle.com/javase/8/docs/technotes/guides/sound/)
- [Audio Processing Articles](https://www.audiosecrets.com/articles)

---

## Contact

For any inquiries or suggestions related to the project, you can reach out to **Somils** at:

- **Email**: somilasd27@gmail.com
- **GitHub**: [somils](https://github.com/somilsdiaz)
