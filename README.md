# SoundUp Advanced Audio Processing and Enhancement Tool


<p align="center">
  <img src="https://github.com/user/SoundUp/raw/main/src/main/java/resources/logo.png" alt="SoundUp Logo" width="200"/>
</p>

## 🎵 Overview

SoundUp is a professional-grade audio enhancement and processing application designed to improve the quality of audio files. Built with Java and leveraging advanced digital signal processing techniques, SoundUp provides a comprehensive suite of tools for audio professionals and enthusiasts alike.

## ✨ Features

### 🔊 Audio Enhancement
- **RMS (Root Mean Square) Processing**: Intelligent volume normalization and dynamic range adjustment
- **PDA (Peak Detection and Analysis)**: Advanced audio analysis and enhancement techniques
- **Multi-parameter Sound Processing**:
  - Bass boost for deeper low frequencies
  - Mid presence enhancement for clearer vocals and instruments
  - High clarity adjustment for crisp high-end detail
  - Stereo width expansion for immersive sound
  - Dynamic range control for consistent volume levels
  - Spatial depth processing for three-dimensional sound
  - Noise reduction for cleaner audio

### 📂 File Management
- Batch processing of multiple files
- Directory tree navigation
- Intelligent file sorting and filtering
- Support for multiple audio formats including WAV, MP3, and more

### 🎛️ User Experience
- Modern and intuitive graphical user interface
- Real-time audio visualization
- Comparative before/after listening options
- Customizable processing parameters

## 🚀 Installation

### Prerequisites
- Java 21 or later
- Maven 3.6.0 or later

### Steps
1. Clone the repository:
   ```bash
   git clone https://github.com/somilsdiaz/SoundUp-Advanced-Audio-Processing-and-Enhancement-Tool
   ```

2. Navigate to the project directory:
   ```bash
   cd SoundUp
   ```

3. Build the project using Maven:
   ```bash
   mvn clean package
   ```

4. Run the application:
   ```bash
   java -jar target/SoundUp-1.0-SNAPSHOT.jar
   ```

## 🔍 Usage

1. **Launch the application** from the JAR file or your IDE
2. From the Start Menu, select your preferred mode:
   - **Single File Processing**: Enhance an individual audio file
   - **Directory Processing**: Process multiple audio files in a directory
3. Configure enhancement parameters according to your needs:
   - Select aby enhancement, betweem RMS or PDA improvements (depending of your needs) 
   - Select 'Convert to stereo' if your music is in mono and you want to convert it to stereo
4. Preview changes and save the enhanced audio files

## 🧩 Architecture

SoundUp is organized into several key modules:

- **RMS**: Handles audio file operations and volume normalization
- **PDA**: Core audio processing and enhancement algorithms
- **VisualComponent**: Audio visualization and user interface elements
- **ConvertirStereo**: Stereo processing and channel management
- **MsgEmergentes**: User interface notifications and dialogs
- **Directorios**: File system navigation and management

## 🔧 Development

### Building from Source
```bash
# Clone the repository
git clone https://github.com/somilsdiaz/SoundUp-Advanced-Audio-Processing-and-Enhancement-Tool

# Navigate to project directory
cd SoundUp

# Build with Maven
mvn clean package
```

### Key Dependencies
- TarsosDSP: Core audio processing library
- JAudioTagger: Audio metadata management
- JNA: Native access for file operations
- Swing: UI components and visualization

## 👥 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📧 Contact

Project Maintainer - [somil.sandoval@gmail.com](mailto:somil.sandoval@gmail.com)

---

<p align="center">
  Made with by Somils
</p> 