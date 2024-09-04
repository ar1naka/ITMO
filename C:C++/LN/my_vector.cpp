#include "my_vector.h"

my_vector::~my_vector()
{
	clear();
}

my_vector &my_vector::operator=(const my_vector &other)
{
	if (this != &other)
	{
		m_size = other.m_size;
		m_capacity = other.m_capacity;
		auto *new_data = (uint8_t *)realloc(m_data, m_capacity * sizeof(uint8_t));
		if (new_data)
		{
			m_data = new_data;
			for (size_t i = 0; i < m_size; i++)
			{
				m_data[i] = other.m_data[i];
			}
		}
		else
		{
			free(m_data);
			throw std::runtime_error("Error with memory!");
		}
	}
	return *this;
}

uint8_t &my_vector::operator[](uint index)
{
	return m_data[index];
}

const uint8_t &my_vector::operator[](uint index) const
{
	return m_data[index];
}

void my_vector::push_back(uint8_t value)
{
	if (!m_capacity)
	{
		m_capacity = 1;
		m_data = (uint8_t *)malloc(sizeof(uint8_t));
		if (m_data == nullptr)
		{
			throw std::runtime_error("Error with memory!");
		}
	}
	else if (m_size == m_capacity)
	{
		m_capacity *= 2;
		uint8_t *old_data = m_data;
		m_data = (uint8_t *)realloc(m_data, m_capacity * sizeof(uint8_t));
		if (m_data == nullptr)
		{
			free(old_data);
			throw std::runtime_error("Error with memory!");
		}
	}
	m_data[m_size++] = value;
}

void my_vector::pop_back()
{
	if (m_size > 0)
	{
		m_size--;
		if (m_size < m_capacity / 2)
		{
			m_capacity /= 2;
			uint8_t *old_data = m_data;
			m_data = (uint8_t *)realloc(m_data, m_capacity * sizeof(uint8_t));
			if (m_data == nullptr)
			{
				free(old_data);
				throw std::runtime_error("Error with memory!");
			}
		}
	}
}

uint8_t &my_vector::back()
{
	return m_data[m_size - 1];
}

size_t my_vector::size() const
{
	return m_size;
}

size_t my_vector::capacity() const
{
	return m_capacity;
}

bool my_vector::empty() const
{
	return m_size == 0;
}

void my_vector::get(size_t count)
{
	for (int i = 0; i < count; i++)
	{
		push_back(0);
	}
}

void my_vector::reverse()
{
	for (int i = 0; i < m_size / 2; i++)
	{
		int temp = m_data[i];
		m_data[i] = m_data[m_size - i - 1];
		m_data[m_size - i - 1] = temp;
	}
}

void my_vector::clear()
{
	if (m_data)
	{
		free(m_data);
		m_data = nullptr;
	}
}
