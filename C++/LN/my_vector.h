#include <cstdlib>
#include <iostream>

typedef unsigned int uint;

class my_vector
{
  public:
	my_vector() : m_size(0), m_capacity(0), m_data() {}

	~my_vector();

	my_vector &operator=(const my_vector &other);

	uint8_t &operator[](uint index);

	const uint8_t &operator[](uint index) const;

	void push_back(uint8_t value);

	void pop_back();

	uint8_t &back();

	size_t size() const;

	size_t capacity() const;

	bool empty() const;

	void reverse();

	void get(size_t s);

	void clear();

  private:
	size_t m_size = 0;
	size_t m_capacity = 0;
	uint8_t *m_data;
};
