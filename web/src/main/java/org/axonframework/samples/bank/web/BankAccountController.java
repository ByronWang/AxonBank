/*
 * Copyright (c) 2016. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.axonframework.samples.bank.web;

//@Controller
//@MessageMapping("/bank-accounts")
public class BankAccountController {
//
//    private CommandBus commandBus;
//    private BankAccountRepository bankAccountRepository;
//
//    @Autowired
//    public BankAccountController(CommandBus commandBus,
//                                 BankAccountRepository bankAccountRepository) {
//        this.commandBus = commandBus;
//        this.bankAccountRepository = bankAccountRepository;
//    }
//
//    @SubscribeMapping
//    public Iterable<BankAccountEntry> all() {
//        return bankAccountRepository.findAllByOrderByIdAsc();
//    }
//
//    @SubscribeMapping("/{id}")
//    public BankAccountEntry get(@DestinationVariable String id) {
//        return bankAccountRepository.findOne(id);
//    }
//
//    @MessageMapping("/create")
//    public void create(MyBankAccountCreateDto bankAccountDto) {
//        String id = UUID.randomUUID().toString();
//        BankAccountCreateCommand command = new BankAccountCreateCommand(id, bankAccountDto.getOverdraftLimit());
//        commandBus.dispatch(GenericCommandMessage.asCommandMessage(command));
//    }
//
//    @MessageMapping("/withdraw")
//    public void withdraw(WithdrawalDto depositDto) {
//        BankAccountWithdrawMoneyCommand command = new BankAccountWithdrawMoneyCommand(depositDto.getBankAccountId(), depositDto.getAmount());
//        commandBus.dispatch(GenericCommandMessage.asCommandMessage(command));
//    }
//
//    @MessageMapping("/deposit")
//    public void deposit(DepositDto depositDto) {
//        BankAccountMoneyDepositCommand command = new BankAccountMoneyDepositCommand(depositDto.getBankAccountId(), depositDto.getAmount());
//        commandBus.dispatch(GenericCommandMessage.asCommandMessage(command));
//    }
}
