Option Explicit
Dim objShell, objFolder
Set objShell = CreateObject("Shell.Application")
Set objFolder = objShell.BrowseForFolder(0, "Seleccione una carpeta:", &H1000, 17)
If objFolder Is Nothing Then
    Wscript.Echo "Cancelado"
Else
    Wscript.Echo objFolder.Self.Path
End If
