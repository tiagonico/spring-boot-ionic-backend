package com.tiago.cursomc.services.validation;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import com.tiago.cursomc.domain.Cliente;
import com.tiago.cursomc.domain.enums.TipoCliente;
import com.tiago.cursomc.dto.ClienteNewDTO;
import com.tiago.cursomc.repositories.ClienteRepository;
import com.tiago.cursomc.resources.exception.FieldMessage;
import com.tiago.cursomc.services.validation.utils.BR;

public class ClienteInsertValidator implements ConstraintValidator<ClienteInsert, ClienteNewDTO> {
	
	@Autowired
	private ClienteRepository clienteRepository;
	
	@Override
	public void initialize(ClienteInsert ann) {
	}

	@Override
	public boolean isValid(ClienteNewDTO objDto, ConstraintValidatorContext context) {
		
		List<FieldMessage> list = new ArrayList<>();
		
		if(objDto.getTipo().equals(TipoCliente.PESSOAFISICA.getCod()) && !BR.isValidCpf(objDto.getCpfOuCnpj()) ) {
			list.add(new FieldMessage("cpfOuCnpj", "CPF inválido"));
		}
		if(objDto.getTipo().equals(TipoCliente.PESSOAJURIDICA.getCod()) && !BR.isValidCnpj(objDto.getCpfOuCnpj()) ) {
			list.add(new FieldMessage("cpfOuCnpj", "CNPJ inválido"));
		}
		
		Cliente auxEmail = clienteRepository.findByEmail(objDto.getEmail());
		Cliente auxCpfOuCnpj = clienteRepository.findByCpfOuCnpj(objDto.getCpfOuCnpj());
		
		if(auxEmail != null ) {
			list.add(new FieldMessage("email", "Email já existente"));
		}
		if(auxCpfOuCnpj != null ) {
			if(auxCpfOuCnpj.getTipo().getCod() ==  TipoCliente.PESSOAFISICA.getCod() ) {
				list.add(new FieldMessage("cpfOuCnpj", "CPF já existente"));
			}
			if(auxCpfOuCnpj.getTipo().getCod() ==  TipoCliente.PESSOAJURIDICA.getCod() ) {
				list.add(new FieldMessage("cpfOuCnpj", "CNPJ já existente"));
			}
			
		}
		
		for (FieldMessage e : list) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(e.getMessage()).addPropertyNode(e.getFieldName())
					.addConstraintViolation();
		}
		return list.isEmpty();
	}
}