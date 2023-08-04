package test.org.springdoc.api.app25.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class Dog extends Pet {

	@Enumerated(EnumType.STRING)
	private CoatType coat;

	@Builder
	public Dog(String name, Owner owner, CoatType coat) {
		super(name, owner);
		this.coat = coat;
	}

	public static enum CoatType {
		SMOOTH, SHORT, COMBINATION, DOUBLE, HEAVY, SILKY, LONG, CURLY, WIRE, HAIRLESS
	}

}
