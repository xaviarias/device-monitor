package net.xas.device.email;

/**
 * An abstract email sender providing mail construction.
 */
public abstract class MailSender {

    public class Mail {

        private String[] from;
        private String[] to;
        private String subject;
        private String body;

        public String[] from() {
            return from;
        }

        public String[] to() {
            return to;
        }

        public String subject() {
            return subject;
        }

        public String body() {
            return body;
        }

        public Mail from(final String... from) {
            this.from = from;
            return this;
        }

        public Mail to(final String... to) {
            this.to = to;
            return this;
        }

        public Mail subject(final String subject) {
            this.subject = subject;
            return this;
        }


        public Mail body(final String body) {
            this.body = body;
            return this;
        }

        public void send() {
            MailSender.this.send(this);
        }
    }

    public abstract void send(MailSender.Mail mail);

}
