package com.david.fixittecnic;

/**
 * Clase que representa la solicitud de acceso al sistema.
 * Funciona como un sobre que contiene las credenciales del usuario para enviarlas
 * de forma organizada al servidor durante el proceso de inicio de sesion.
 */
public class LoginRequest {
    private String email;
    private String password;

    /**
     * Constructor para preparar los datos de acceso.
     * @param email Direccion de correo electronico del tecnico.
     * @param password Clave secreta para entrar en la cuenta.
     */
    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    /**
     * Recupera el correo electronico introducido por el usuario.
     * @return El texto del email.
     */
    public String getEmail() { return email; }

    /**
     * Recupera la contraseña introducida por el usuario.
     * @return El texto de la clave secreta.
     */
    public String getPassword() { return password; }
}