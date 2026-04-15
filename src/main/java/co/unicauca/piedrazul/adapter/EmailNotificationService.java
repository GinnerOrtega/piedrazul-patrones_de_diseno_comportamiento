package co.unicauca.piedrazul.adapter;

/**
 * Servicio externo de email con interfaz incompatible con el dominio. Simula
 * una librería de terceros que no podemos modificar.
 *
 * @author GINNER
 */
public class EmailNotificationService {

    public void sendEmail(String recipient, String subject, String body) {
        System.out.println("==============================");
        System.out.println("📧 EMAIL ENVIADO");
        System.out.println("Para:    " + recipient);
        System.out.println("Asunto:  " + subject);
        System.out.println("Cuerpo:  " + body);
        System.out.println("==============================");
    }
}
