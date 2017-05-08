package org.axonframework.samples.bankcqrssrc.generatedsources;

import java.util.concurrent.Callable;
import java.util.function.Consumer;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.Aggregate;
import org.axonframework.commandhandling.model.Repository;
import org.axonframework.eventhandling.EventBus;

public class MyBankAccountCommandHandler {
	private Repository<MyBankAccountImpl> repository;
	private EventBus eventBus;

	public MyBankAccountCommandHandler(Repository<MyBankAccountImpl> repository, EventBus eventBus) {
		this.repository = repository;
		this.eventBus = eventBus;
	}

	@CommandHandler
	public void handle(MyBankAccountCreateCommand command) throws Exception {
		InnerCreate caller = new InnerCreate(command);
		this.repository.newInstance(caller);
	}

	@CommandHandler
	public void handle(MyBankAccountDepositCommand command) {
		String axonBankAccountId = command.getAxonBankAccountId();
		Aggregate<MyBankAccountImpl> aggregate = this.repository.load(axonBankAccountId);
		InnerDeposit caller = new InnerDeposit(command);
		aggregate.execute(caller);
	}

	@CommandHandler
	public void handle(MyBankAccountWithdrawCommand command) {
		String axonBankAccountId = command.getAxonBankAccountId();
		Aggregate<MyBankAccountImpl> aggregate = this.repository.load(axonBankAccountId);
		InnerWithdraw caller = new InnerWithdraw(command);
		aggregate.execute(caller);
	}

	class InnerDeposit implements Consumer<MyBankAccountImpl> {
		MyBankAccountDepositCommand command;

		InnerDeposit(MyBankAccountDepositCommand command) {
			this.command = command;
		}

		public void accept(MyBankAccountImpl domain) {
			domain.deposit(this.command.getAmount());
		}
	}

	class InnerWithdraw implements Consumer<MyBankAccountImpl> {
		MyBankAccountWithdrawCommand command;

		InnerWithdraw(MyBankAccountWithdrawCommand command) {
			this.command = command;
		}

		public void accept(MyBankAccountImpl domain) {
			domain.withdraw(this.command.getAmount());
		}
	}

	class InnerCreate implements Callable<MyBankAccountImpl> {
		MyBankAccountCreateCommand command;

		InnerCreate(MyBankAccountCreateCommand command) {
			this.command = command;
		}

		public MyBankAccountImpl call() throws Exception {
			return new MyBankAccountImpl(this.command.getAxonBankAccountId(), this.command.getOverdraftLimit());
		}
	}
}
