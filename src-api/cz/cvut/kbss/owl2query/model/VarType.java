package cz.cvut.kbss.owl2query.model;

import java.util.Collection;

public enum VarType {
	CLASS(new OWLObjectType[] { OWLObjectType.OWLClass }),

	OBJECT_OR_DATA_PROPERTY(new OWLObjectType[] {
			OWLObjectType.OWLObjectProperty, OWLObjectType.OWLDataProperty }),

	OBJECT_PROPERTY(new OWLObjectType[] { OWLObjectType.OWLObjectProperty }),

	DATA_PROPERTY(new OWLObjectType[] { OWLObjectType.OWLDataProperty }),

	INDIVIDUAL(new OWLObjectType[] { OWLObjectType.OWLNamedIndividual }),

	LITERAL(new OWLObjectType[] { OWLObjectType.OWLLiteral }),

	INDIVIDUAL_OR_LITERAL(new OWLObjectType[] {
			OWLObjectType.OWLNamedIndividual, OWLObjectType.OWLLiteral });

	private OWLObjectType[] allowed;

	private VarType(OWLObjectType[] allowed) {
		this.allowed = allowed;
	}

	public OWLObjectType[] getAllowedTypes() {
		return allowed;
	}

	public boolean updateIfValid(Collection<VarType> vars) {
		if (vars.contains(this)) {
			return true;
		}

		switch (this) {
		case CLASS:
			if (vars.contains(LITERAL)) {
				return false;
			} else if (vars.contains(INDIVIDUAL_OR_LITERAL)) {
				vars.remove(INDIVIDUAL_OR_LITERAL);
				vars.add(INDIVIDUAL);
			}
			vars.add(CLASS);
			break;
		case INDIVIDUAL:
			if (vars.contains(LITERAL)) {
				return false;
			} else if (vars.contains(INDIVIDUAL_OR_LITERAL)) {
				vars.remove(INDIVIDUAL_OR_LITERAL);
			}
			vars.add(INDIVIDUAL);
			break;
		case DATA_PROPERTY:
			if (vars.contains(LITERAL)) {
				return false;
			} else if (vars.contains(INDIVIDUAL_OR_LITERAL)) {
				vars.remove(INDIVIDUAL_OR_LITERAL);
				vars.add(INDIVIDUAL);
			}

			if (vars.contains(OBJECT_OR_DATA_PROPERTY)) {
				vars.add(OBJECT_OR_DATA_PROPERTY);
			}
			vars.add(DATA_PROPERTY);
			break;
		case OBJECT_OR_DATA_PROPERTY:
			if (vars.contains(LITERAL)) {
				return false;
			} else if (vars.contains(INDIVIDUAL_OR_LITERAL)) {
				vars.remove(INDIVIDUAL_OR_LITERAL);
				vars.add(INDIVIDUAL);
			}

			if (!vars.contains(OBJECT_PROPERTY)
					&& !vars.contains(DATA_PROPERTY)) {
				vars.add(OBJECT_OR_DATA_PROPERTY);
			}
			break;
		case OBJECT_PROPERTY:
			if (vars.contains(LITERAL)) {
				return false;
			} else if (vars.contains(INDIVIDUAL_OR_LITERAL)) {
				vars.remove(INDIVIDUAL_OR_LITERAL);
				vars.add(INDIVIDUAL);
			}

			if (vars.contains(OBJECT_OR_DATA_PROPERTY)) {
				vars.add(OBJECT_OR_DATA_PROPERTY);
			}
			vars.add(OBJECT_PROPERTY);
			break;
		case INDIVIDUAL_OR_LITERAL:
			if (vars.contains(INDIVIDUAL)) {
				return true;
			} else if (vars.contains(CLASS) || vars.contains(OBJECT_PROPERTY)
					|| vars.contains(OBJECT_OR_DATA_PROPERTY)
					|| vars.contains(DATA_PROPERTY)) {
				vars.add(INDIVIDUAL);
			} else if (!vars.contains(VarType.INDIVIDUAL)
					&& !vars.contains(VarType.LITERAL)) {
				vars.add(INDIVIDUAL_OR_LITERAL);
			}
			break;
		case LITERAL:
			if (vars.contains(vars.contains(CLASS)
					|| vars.contains(OBJECT_PROPERTY)
					|| vars.contains(OBJECT_OR_DATA_PROPERTY)
					|| vars.contains(DATA_PROPERTY)
					|| vars.contains(INDIVIDUAL))) {
				return false;
			} else if (vars.contains(INDIVIDUAL_OR_LITERAL)) {
				vars.remove(INDIVIDUAL_OR_LITERAL);
				vars.add(LITERAL);
			}
			break;
		default:
			;

		}
		return true;
	}
}
