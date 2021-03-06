package test.task.view;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import org.springframework.beans.factory.annotation.Autowired;
import test.task.entity.Credit;
import test.task.entity.Bank;
import test.task.operations.BankOperations;
import test.task.operations.CreditOperations;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@SpringView(name = "Credits")
public class ViewCredit extends VerticalLayout implements View {
    @Autowired
    private CreditOperations creditOperations;

    @Autowired
    private BankOperations bankOperations;

    private Button add;
    private Button delete;
    private Button update;
    private TextField name;
    private TextField creditLimit;
    private TextField percent;

    private List<Credit> credits;
    private Grid<Credit> grid;

    HorizontalLayout horizontalLayout = new HorizontalLayout();
    NativeSelect<Bank> bankNativeSelect;
    @PostConstruct

    void init() {
        addComponent(horizontalLayout);
        showAllCredit();
        horizontalLayout.addComponent(add = new Button("Add credit",event -> getUI().addWindow(createUpdateCredit())));
        horizontalLayout.addComponent(update = new Button("Edit credit",event -> getUI().addWindow(createUpdateCredit(credits.get(0)))));
        horizontalLayout.addComponent(delete = new Button("Remove credit",event ->{creditOperations.deleteAll(credits);getUI().getNavigator().navigateTo("Credits");}));

        add.setIcon(VaadinIcons.MONEY);
        update.setIcon(VaadinIcons.MONEY);
        delete.setIcon(VaadinIcons.MONEY);

        delete.setEnabled(false);
        update.setEnabled(false);
    }

    private Window createUpdateCredit(){
        Credit credit = new Credit();
        Window window = new Window("Credit");
        VerticalLayout verticalWindow = new VerticalLayout();
        bankNativeSelect = new NativeSelect<Bank>("Select Bank",bankOperations.getAll());
        bankNativeSelect.setItemCaptionGenerator(Bank ::getName);
        window.setContent(verticalWindow);
        verticalWindow.addComponent(name = new TextField("Name"));
        verticalWindow.addComponent(creditLimit= new TextField("Limit"));
        verticalWindow.addComponent(percent = new TextField("Percent"));
        verticalWindow.addComponent(bankNativeSelect);
        bankNativeSelect.setIcon(VaadinIcons.PIGGY_BANK);
        return getComponents(credit, window, verticalWindow);
    }
    private Window createUpdateCredit(Credit credit){
        Window window = new Window("Credit");
        VerticalLayout verticalWindow = new VerticalLayout();
        bankNativeSelect = new NativeSelect<Bank>("Select Bank",bankOperations.getAll());
        bankNativeSelect.setItemCaptionGenerator(Bank ::getName);
        window.setContent(verticalWindow);
        verticalWindow.addComponent(name = new TextField("Name",credit.getName()));
        verticalWindow.addComponent(creditLimit= new TextField("Limit",String.valueOf(credit.getCreditLimit())));
        verticalWindow.addComponent(percent = new TextField("Name",String.valueOf(credit.getCreditPercent())));
        verticalWindow.addComponent(bankNativeSelect);
        bankNativeSelect.setIcon(VaadinIcons.PIGGY_BANK);

        return getComponents(credit, window, verticalWindow);
    }

    private Window getComponents(Credit credit, Window window, VerticalLayout verticalWindow) {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.addComponent(new Button("OK", event -> {
            credit.setName(name.getValue());
            credit.setCreditLimit(Double.parseDouble(creditLimit.getValue()));
            credit.setCreditPercent(Double.parseDouble(percent.getValue()));
            credit.setBank(bankNativeSelect.getValue());
            creditOperations.create(credit);
            window.close();
            getUI().getNavigator().navigateTo("Credits");
        }));

        horizontalLayout.addComponent(new Button("CLOSE", event -> window.close()));
        verticalWindow.addComponent(horizontalLayout);
        window.setModal(true);
        window.center();
        return window;
    }

    private void showAllCredit() {
        grid = new Grid<>(Credit.class);
        grid.setItems(creditOperations.getAll());
        grid.removeAllColumns();
        grid.setWidth("34%");
        grid.addColumn(Credit :: getCreditLimit)
                .setCaption("Credit limit");
        grid.addColumn(Credit :: getCreditPercent)
                .setCaption("Percent");
        grid.addColumn(Credit :: getBankName)
                .setCaption("Bank");
        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        grid.addSelectionListener(event -> {
            credits = new ArrayList<>(event.getAllSelectedItems());
            delete.setEnabled(credits.size() > 0);
            update.setEnabled(credits.size() == 1);
        });
        addComponents(grid);

    }
}
