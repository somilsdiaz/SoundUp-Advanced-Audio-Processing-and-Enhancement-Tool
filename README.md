# SoundUp - Audio Processing Application

**SoundUp** es una aplicación avanzada para la gestión y procesamiento de archivos de audio, diseñada para tareas como normalización de volumen, eliminación de duplicados, y mejoras de calidad de audio. El proyecto está desarrollado principalmente en **Java** y utiliza una serie de bibliotecas especializadas para la manipulación de archivos de audio.

## Descripción del Proyecto

SoundUp permite a los usuarios cargar archivos de audio, aplicar normalizaciones y mejoras, y visualizar comparaciones entre los archivos originales y los procesados. Además, la interfaz gráfica facilita la interacción del usuario con las herramientas avanzadas que ofrece el sistema. La aplicación también incluye un visualizador de audio en tiempo real que representa gráficamente la amplitud del sonido durante la reproducción.

### Principales Funcionalidades
- **Normalización de audio**: Aumenta el volumen de los archivos de audio de manera uniforme, sin reducir la calidad.
- **Mejoras de sonido**: Incrementa la calidad de graves, medios, brillos y otras características del sonido.
- **Eliminación de duplicados**: Elimina los archivos de audio convertidos que ya han sido normalizados o mejorados.
- **Interfaz gráfica amigable**: Permite a los usuarios visualizar comparaciones entre los archivos originales y los procesados, junto con una representación gráfica del audio en tiempo real mediante barras que indican la amplitud del sonido en distintos momentos.
  
---

## Funcionalidades y Estructura General del Código

**Funcionalidades Comunes**:
- **Visualización de Archivos**: Carga y muestra los archivos de audio originales y procesados, permitiendo a los usuarios realizar comparaciones directas. Además, el componente `AudioVisualizer` dibuja barras en tiempo real para representar la amplitud de las ondas de audio procesadas.
- **Manejo de Rutas**: Asegura que la aplicación tenga acceso a las rutas correctas de los archivos de audio.
- **Interacción con el Usuario**: Maneja eventos como minimizar o cerrar ventanas, y gestiona la eliminación de archivos temporales para mantener el sistema limpio.
- **Animación y Feedback Visual**: Utiliza componentes visuales, como un círculo de carga, para indicar que el procesamiento está en curso, mejorando la experiencia del usuario. Durante la reproducción, el `AudioVisualizer` actualiza las barras visuales para representar las amplitudes del audio.

**Estructura General del Código**:
- **Constructores**: Inicializan las interfaces gráficas y los componentes necesarios, configurando los paneles y animaciones adecuadamente. El visualizador de audio `AudioVisualizer` es una extensión de `JPanel`, configurado para recibir los bytes del archivo de audio como entrada y dibujar las barras de amplitud en la interfaz gráfica.
- **Métodos de Procesamiento**: Incluyen lógica para limpiar nombres de archivo y gestionar la visualización. En el caso del `AudioVisualizer`, los bytes de audio se transforman en datos visuales que son convertidos en barras verticales, cuya altura representa la amplitud del audio.
- **Métodos de Redibujo y Actualización**: Actualizan la interfaz gráfica en respuesta a eventos. El método `paintComponent` en `AudioVisualizer` se encarga de dibujar tanto el fondo como las barras de amplitud. Además, un `Timer` se utiliza para actualizar las alturas de las barras (`BarHeights`) a intervalos regulares, lo que permite una visualización fluida y dinámica del audio.

---

## Contribuciones

Las contribuciones son bienvenidas. Si deseas contribuir al proyecto, sigue los siguientes pasos:

1. Haz un fork del repositorio.
2. Crea una nueva rama con tu funcionalidad (`git checkout -b feature/AmazingFeature`).
3. Realiza los cambios necesarios.
4. Asegúrate de que los cambios no rompen el proyecto (`./gradlew test`).
5. Realiza un pull request.

### Requerimientos de Contribución

- Sigue el estilo de codificación y convenciones utilizadas en el proyecto.
- Mantén un historial de commits limpio y claro.
- Documenta los cambios en el archivo `CHANGELOG.md`.

---

## Licencia

Este proyecto está licenciado bajo la Licencia MIT - consulta el archivo [LICENSE](LICENSE) para más detalles.

---

## Recursos Adicionales

Si tienes dudas o problemas, puedes consultar la siguiente documentación y recursos:

- [Documentación de Java](https://docs.oracle.com/javase/8/docs/)
- [Guía de uso de JAudioTagger](http://www.jthink.net/jaudiotagger/)
- [Tutoriales de Swing](https://docs.oracle.com/javase/tutorial/uiswing/)

---

## Contacto

Para cualquier consulta o sugerencia relacionada con el proyecto, puedes contactar a **Somils** a través de:

- **Email**: somils@example.com
- **GitHub**: [somils](https://github.com/somils)
