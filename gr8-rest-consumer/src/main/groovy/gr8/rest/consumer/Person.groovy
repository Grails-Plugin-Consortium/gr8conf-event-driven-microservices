package gr8.rest.consumer

class Person {
	Long id
	String name


	@Override
	public String toString() {
		return "I am a Person{" +
				"id=" + id +
				", name='" + name + '\'' +
				'}';
	}
}
