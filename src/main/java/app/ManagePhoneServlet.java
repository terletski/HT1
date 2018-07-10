package app;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class ManagePhoneServlet extends HttpServlet {

    private final String INCORRECT_PHONE_MESSAGE = "Телефонный номер должен содержать от 2 до 50 символов, и состоять из цифр, +, -, #.";

    // Идентификатор для сериализации/десериализации.
    private static final long serialVersionUID = 1L;

    // Основной объект, хранящий данные телефонной книги.
    private Phonebook phonebook;

    public ManagePhoneServlet() {
        // Вызов родительского конструктора.
        super();

        // Создание экземпляра телефонной книги.
        try {
            this.phonebook = Phonebook.getInstance();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

    }

    private String validatePhoneNumber(String phone) {
        if (Person.isPhoneValid(phone)) {
            return "";
        } else {
            return INCORRECT_PHONE_MESSAGE;
        }
    }

    // GET-запросы.
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Обязательно до обращения к любому параметру нужно переключиться в UTF-8,
        request.setCharacterEncoding("UTF-8");

        // В JSP нам понадобится сама телефонная книга. Можно создать её экземпляр там,
        // Создаём экземпляр телефонной книги и передаём в JSP.
        request.setAttribute("phonebook", this.phonebook);

        // Хранилище параметров для передачи в JSP.
        HashMap<String, String> jsp_parameters = new HashMap<String, String>();

        // Диспетчеры для передачи управления на разные JSP.
        RequestDispatcher dispatcher_for_manager = request.getRequestDispatcher("/ManagePerson.jsp");
        RequestDispatcher dispatcher_for_phone = request.getRequestDispatcher("/ManagePhone.jsp");

        // Действие (action) и идентификатор записи (id) над которой выполняется это действие.
        String action = request.getParameter("action");
        String id = request.getParameter("id");
        String idPhone = request.getParameter("idPhone");

        switch (Operations.valueOf(action)) {

            // Редактирование номера телефона
            case edit:
                // Извлечение из телефонной книги информации о редактируемой записи.
                Person personForEditPhone = this.phonebook.getPerson(id);

                // Подготовка параметров для JSP.
                jsp_parameters.put("current_action", "edit");
                jsp_parameters.put("next_action", "edit_go");
                jsp_parameters.put("next_action_label", "Сохранить");

                // Установка параметров JSP.
                request.setAttribute("person", personForEditPhone);
                request.setAttribute("idPhone", idPhone);
                request.setAttribute("jsp_parameters", jsp_parameters);

                // Передача запроса в JSP.
                dispatcher_for_phone.forward(request, response);
                break;

            // Удаление номера телефона
            case delete:

                Person person = this.phonebook.getPerson(id);
                // Если запись удалось удалить...
                if(this.phonebook.deletePhoneNumberFomPerson(person, idPhone)){
                    jsp_parameters.put("current_action_result", "DELETION_SUCCESS");
                    jsp_parameters.put("current_action_result_label", "Удаление выполнено успешно");

                    jsp_parameters.put("next_action_label", "Сохранить");
                }// Если запись не удалось удалить (например, такой записи нет)...
                else {
                    jsp_parameters.put("current_action_result", "DELETION_FAILURE");
                    jsp_parameters.put("current_action_result_label", "Ошибка удаления (возможно, запись не найдена)");
                }
                // Установка параметров JSP.
                request.setAttribute("person", person);
                request.setAttribute("jsp_parameters", jsp_parameters);

                // Передача запроса в JSP.
                dispatcher_for_manager.forward(request, response);
                break;

            // Добавление нового номера телефона
            case add:
                Person personForAddPhone = this.phonebook.getPerson(id);

                // Подготовка параметров для JSP.
                jsp_parameters.put("current_action", "add");
                jsp_parameters.put("next_action", "add_go");
                jsp_parameters.put("next_action_label", "Добавить");

                // Установка параметров JSP.
                request.setAttribute("person", personForAddPhone);
                request.setAttribute("jsp_parameters", jsp_parameters);

                // Передача запроса в JSP.
                dispatcher_for_phone.forward(request, response);
                break;
        }
    }


    // Реакция на POST-запросы.
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Обязательно до обращения к любому параметру нужно переключиться в UTF-8,
        request.setCharacterEncoding("UTF-8");

        request.setAttribute("phonebook", this.phonebook);

        // Хранилище параметров для передачи в JSP.
        HashMap<String, String> jsp_parameters = new HashMap<String, String>();

        // Диспетчеры для передачи управления на разные JSP.
        RequestDispatcher dispatcher_for_manager = request.getRequestDispatcher("/ManagePerson.jsp");
        RequestDispatcher dispatcher_for_phone = request.getRequestDispatcher("/ManagePhone.jsp");

        // Действие (add_go, edit_go) и идентификатор записи (id) над которой выполняется это действие.
        String id = request.getParameter("id");
        String addPhoneGo = request.getParameter("addPhoneGo");
        String editPhoneGo = request.getParameter("editPhoneGo");
        String idPhone = request.getParameter("idPhone");
        String phone = request.getParameter("phone");

        //изменение номера телефона
        if (editPhoneGo != null) {
            Person person = this.phonebook.getPerson(id);
            request.setAttribute("person", person);
            String error_message = validatePhoneNumber(phone);
            // Если данные верные, можно производить добавление.
            if (error_message.isEmpty()) {
                if(this.phonebook.editPhoneNumberToPerson(person, idPhone, phone)){
                    jsp_parameters.put("current_action_result", "UPDATE_SUCCESS");
                    jsp_parameters.put("current_action_result_label", "Обновление выполнено успешно");
                    jsp_parameters.put("next_action_label", "Сохранить");

                }// Если запись НЕ удалось обновить...
                else {
                    jsp_parameters.put("current_action_result", "UPDATE_FAILURE");
                    jsp_parameters.put("current_action_result_label", "Ошибка обновления");
                }
                // Установка параметров JSP.
                request.setAttribute("jsp_parameters", jsp_parameters);
                // Передача запроса в JSP.
                dispatcher_for_manager.forward(request, response);
            }    // Если в данных были ошибки, надо заново показать форму и сообщить об ошибках.
            else {
                // Подготовка параметров для JSP.
                jsp_parameters.put("current_action", "edit");
                jsp_parameters.put("next_action", "edit_go");
                jsp_parameters.put("next_action_label", "Сохранить");
                jsp_parameters.put("error_message", error_message);
                // Установка параметров JSP.
                request.setAttribute("jsp_parameters", jsp_parameters);
                // Передача запроса в JSP.
                dispatcher_for_phone.forward(request, response);
            }
        }

        //добавление номера телефона
        if (addPhoneGo != null) {
            Person person = this.phonebook.getPerson(id);
            request.setAttribute("person", person);
            String error_message = validatePhoneNumber(phone);

            // Если данные верные, можно производить добавление.
            if (error_message.isEmpty()) {
                // Если запись удалась добавить...
                if(this.phonebook.addPhoneNumberToPerson(person, phone)){
                    jsp_parameters.put("current_action_result_label", "Добавление выполнено успешно");
                    jsp_parameters.put("next_action_label", "Сохранить");
                }
                // Если запись НЕ удалась добавить...
                else {
                    jsp_parameters.put("current_action_result", "ADDITION_FAILURE");
                    jsp_parameters.put("current_action_result_label", "Ошибка добавления");
                }
                // Установка параметров JSP.
                request.setAttribute("jsp_parameters", jsp_parameters);
                // Передача запроса в JSP.
                dispatcher_for_manager.forward(request, response);
            }
            // Если в данных были ошибки, надо заново показать форму и сообщить об ошибках.
            else {
                // Подготовка параметров для JSP.
                jsp_parameters.put("current_action", "add");
                jsp_parameters.put("next_action", "add_go");
                jsp_parameters.put("next_action_label", "Добавить");
                jsp_parameters.put("error_message", error_message);

                // Установка параметров JSP.
                request.setAttribute("jsp_parameters", jsp_parameters);
                // Передача запроса в JSP.
                dispatcher_for_phone.forward(request, response);
            }
        }
    }
}



