package co.unicauca.piedrazul.adapter;

/**
 * Servicio externo de SMS con interfaz incompatible con el dominio. Simula una
 * librería de terceros que no podemos modificar.
 *
 * @author GINNER
 */
public class SmsNotificationService {

    public void sendSms(String phoneNumber, String message) {
        System.out.println("==============================");
        System.out.println("📱 SMS ENVIADO");
        System.out.println("Teléfono: " + phoneNumber);
        System.out.println("Mensaje:  " + message);
        System.out.println("==============================");
    }
}
