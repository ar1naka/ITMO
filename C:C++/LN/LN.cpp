#include "LN.h"

#include <climits>
#include <cmath>
#include <cstring>

LN::LN(long long int x)
{
	if (x < 0)
	{
		m_isNeg = true;
	}
	do
	{
		m_value.push_back(x % 16);
		x /= 16;
	} while (x);
}

LN::LN(const LN &a)
{
	this->m_isNan = a.m_isNan;
	this->m_isNeg = a.m_isNeg;
	this->m_value = a.m_value;
}

LN::LN(LN &&a) noexcept
{
	this->m_isNan = a.m_isNan;
	this->m_isNeg = a.m_isNeg;
	this->m_value = a.m_value;
	a.m_value.clear();
	a.m_isNeg = false;
}

LN::LN(const char *str)
{
	size_t len = strlen(str);
	m_isNeg = (str[0] == '-');
	m_isNan = false;
	for (size_t i = m_isNeg ? 1 : 0; i < len; i++)
	{
		uint8_t k = str[i];
		if (k >= 'A' && k <= 'F')
		{
			k = k - 'A' + 10;
		}
		else if ((k >= 'a' && k <= 'f'))
		{
			k = k - 'a' + 10;
		}
		else
		{
			k = k - '0';
		}
		m_value.push_back(k);
	}
	m_value.reverse();
}

LN::LN(std::string_view str)
{
	size_t len = str.length();
	m_isNeg = (str[0] == '-');
	for (size_t i = m_isNeg ? 1 : 0; i < len; i++)
	{
		uint8_t k = str[i];
		if (k >= 'A' && k <= 'F')
		{
			k = k - 'A' + 10;
		}
		else if ((k >= 'a' && k <= 'f'))
		{
			k = k - 'a' + 10;
		}
		else
		{
			k = k - '0';
		}
		m_value.push_back(k);
	}
	m_value.reverse();
}

LN::LN(const my_vector &m_value)
{
	this->m_value = m_value;
}
LN::LN(my_vector &res, bool negate, bool nan)
{
	this->m_value = res;
	this->m_isNeg = negate;
	this->m_isNan = nan;
}

LN::~LN()
{
	m_value.clear();
}

LN::operator bool() const
{
	if (m_isNan)
	{
		return false;
	}
	if (*this == 0_ln)
	{
		return false;
	}
	return true;
}

LN::operator long long() const
{
	if (m_isNan)
	{
		return NAN;
	}
	LN ma(LLONG_MAX);
	LN mi(LLONG_MIN);
	if (*this > ma || *this < mi)
	{
		throw std::runtime_error("Wrong format for converting to LONG!");
	}
	long long res = 0;
	int step = 0;
	for (int i = 0; i < m_value.size(); i++, step *= 16)
	{
		res += m_value[i] * step;
	}
	return res;
}

LN operator+(const LN &a, const LN &b)
{
	my_vector res;
	if (a.m_isNeg && b.m_isNeg)
	{
		res = LN::add(-a, -b);
		return LN(res, true, false);
	}
	if (a.m_isNeg && !b.m_isNeg)
	{
		return b - (-a);
	}
	if (b.m_isNeg && !a.m_isNeg)
	{
		return a - (-b);
	}
	res = LN::add(a, b);
	LN c(res);
	if (a.m_isNan || b.m_isNan)
	{
		c.m_isNan = true;
		return c;
	}
	return c;
}

LN &LN::operator+=(const LN &ln)
{
	return *this = *this + ln;
}

LN operator-(const LN &a, const LN &b)
{
	my_vector res;
	if (a.m_isNeg || b.m_isNeg)
	{
		res = LN::add(a, -b);
		return LN(res);
	}
	if (a < b)
	{
		res = LN::sub(b, a);
		return LN(res, true, false);
	}
	res = LN::sub(a, b);
	LN c(res);
	if (a.m_isNan || b.m_isNan)
	{
		c.m_isNan = true;
	}
	return c;
}

LN &LN::operator-=(const LN &ln)
{
	return *this = *this - ln;
}

LN operator*(const LN &a, const LN &b)
{
	bool m_isNeg = a.m_isNeg ^ b.m_isNeg;
	my_vector res;
	res.get(a.size() + b.size());
	if (a.m_isNan || b.m_isNan)
	{
		return LN(res, m_isNeg, true);
	}
	if (a == 0_ln || b == 0_ln)
	{
		return LN(0_ln);
	}
	for (int i = 0; i < a.size(); i++)
	{
		for (int j = 0; j < b.size(); j++)
		{
			res[i + j] += a.m_value[i] * b.m_value[j];
			res[i + j + 1] += res[i + j] / 16;
			res[i + j] %= 16;
		}
	}
	while (!res.empty() && res.back() == 0 && res.size() > 1)
	{
		res.pop_back();
	}
	return LN(res, m_isNeg, false);
}

LN &LN::operator*=(const LN &ln)
{
	return *this = *this * ln;
}

LN operator/(const LN &a, const LN &b)
{
	LN x(a);
	LN y(b);
	x.m_isNeg = false;
	y.m_isNeg = false;
	LN c;
	if (a.size() < b.size())
	{
		return 0_ln;
	}
	c.m_value.get(a.size() - b.size() + 1);
	if (a.m_isNan || b.m_isNan)
	{
		c.m_isNan = true;
		return c;
	}
	if (b == 0_ln)
	{
		c.m_isNan = true;
		return c;
	}
	for (uint i = c.size() - 1; i != -1; --i)
	{
		while (y * c <= x)
		{
			c.m_value[i]++;
		}
		c.m_value[i]--;
	}
	while (c.size() > 1 && !c.m_value.back())
	{
		c.m_value.pop_back();
	}
	bool m_isNeg = a.m_isNeg ^ b.m_isNeg;
	c.m_isNeg = m_isNeg;
	return c;
}

LN &LN::operator/=(const LN &ln)
{
	return *this = *this / ln;
}

LN operator%(const LN &a, const LN &b)
{
	my_vector res;
	LN x(a);
	LN y(b);
	x.m_isNeg = false;
	y.m_isNeg = false;
	LN c(res);
	if (a.m_isNan || b.m_isNan)
	{
		c.m_isNan = true;
	}
	c = x - (x / y) * y;
	if (a.m_isNeg)
	{
		c.m_isNeg = true;
	}
	else
	{
		c.m_isNeg = false;
	}
	bool m_isNeg = a.m_isNeg ^ b.m_isNeg;
	c.m_isNeg = m_isNeg;
	return c;
}

LN &LN::operator%=(const LN &ln)
{
	return *this = *this % ln;
}

LN LN::operator-() const
{
	LN unary(*this);
	unary.m_isNeg = !unary.m_isNeg;
	if (*this == 0_ln)
	{
		return LN(0_ln);
	}
	return unary;
}

LN LN::operator~() const
{
	LN result;
	if (m_isNeg)
	{
		result.m_isNan = true;
		return result;
	}
	if (m_isNan)
	{
		return result;
	}
	result.m_value.pop_back();
	result.m_value.get(this->size());
	for (uint i = result.size() - 1; i != -1; --i)
	{
		for (int j = 0; j < 16; j++)
		{
			LN temp = result * result;
			if (temp > *this)
			{
				break;
			}
			result.m_value[i]++;
		}
		result.m_value[i]--;
	}
	while (!result.m_value.empty() && result.m_value.back() == 0 && result.m_value.size() > 1)
	{
		result.m_value.pop_back();
	}
	return result;
}

bool operator>(const LN &a, const LN &b)
{
	return !(a <= b);
}

bool operator>=(const LN &a, const LN &b)
{
	return !(a < b);
}

bool operator<(const LN &a, const LN &b)
{
	if (a.m_isNan || b.m_isNan)
	{
		return a != b;
	}
	if (LN::compare(a, b) < 0)
	{
		return true;
	}
	else
	{
		return false;
	}
}

bool operator<=(const LN &a, const LN &b)
{
	return (a < b || a == b);
}

bool operator==(const LN &a, const LN &b)
{
	if (a.m_isNeg != b.m_isNeg)
		return false;
	if (a.size() != b.size())
		return false;
	for (int i = 0; i < a.size(); ++i)
	{
		if (a.m_value[i] != b.m_value[i])
			return false;
	}
	return true;
}

bool operator!=(const LN &a, const LN &b)
{
	return !(a == b);
}

LN &LN::operator=(const LN &a)
{
	this->m_isNan = a.m_isNan;
	this->m_isNeg = a.m_isNeg;
	this->m_value = a.m_value;
	return *this;
}

LN &LN::operator=(LN &&ln) noexcept
{
	this->m_isNan = ln.m_isNan;
	this->m_isNeg = ln.m_isNeg;
	this->m_value = ln.m_value;
	ln.m_isNeg = false;
	ln.m_value.clear();
	return *this;
}

LN operator"" _ln(const char num[])
{
	return LN(num);
}

void LN::convert(std::ostream &out)
{
	static const char digits[] = "0123456789ABCDEF";
	if (m_isNan)
	{
		out << "NaN";
	}
	else
	{
		if (m_isNeg)
		{
			out << '-';
		}
		for (uint i = m_value.size() - 1; i != -1; --i)
		{
			out << digits[m_value[i]];
		}
	}
}

my_vector LN::add(const LN &a, const LN &b)
{
	my_vector res;
	uint8_t carry = 0;
	uint64_t mi = std::min(a.size(), b.size());
	uint64_t ma = std::max(a.size(), b.size());
	for (int i = 0; i < ma; i++)
	{
		if (i < mi)
			carry += a.m_value[i] + b.m_value[i];
		else if (a.size() > b.size())
			carry += a.m_value[i];
		else
			carry += b.m_value[i];
		res.push_back(carry % 16);
		carry /= 16;
	}
	if (carry)
	{
		res.push_back(carry);
	}
	return res;
}

my_vector LN::sub(const LN &a, const LN &b)
{
	my_vector res;
	uint8_t carry = 0;
	for (int i = 0; i < a.size(); ++i)
	{
		int cur_diff = a.m_value[i] - carry - (i < b.size() ? b.m_value[i] : 0);
		carry = cur_diff < 0;
		res.push_back((16 + cur_diff) % 16);
	}
	while (!res.empty() && res.back() == 0 && res.size() > 1)
	{
		res.pop_back();
	}
	return res;
}

int LN::compare(const LN &a, const LN &b)
{
	if (a.m_isNeg != b.m_isNeg)
	{
		if (a.m_isNeg < b.m_isNeg)
		{
			return 1;
		}
		else
		{
			return -1;
		}
	}
	if (a.size() != b.size())
	{
		if ((a.size() > b.size()) ^ a.m_isNeg)
		{
			return 1;
		}
		else
		{
			return -1;
		}
	}
	for (int i = 0; i < a.size(); i++)
	{
		uint index = a.size() - i - 1;
		if (a.m_value[index] != b.m_value[index])
		{
			if ((a.m_value[index] > b.m_value[index]) ^ a.m_isNeg)
			{
				return 1;
			}
			else
			{
				return -1;
			}
		}
	}
	return 0;
}
