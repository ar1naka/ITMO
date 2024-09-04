#include "my_vector.h"
#include "return_codes.h"
#include <string_view>

#include <cstdlib>
#include <vector>

class LN
{
  public:
	explicit LN(long long x = 0);
	LN(const LN&);
	LN(LN&&) noexcept;
	explicit LN(const char* str);
	explicit LN(std::string_view);
	~LN();

	explicit operator bool() const;
	explicit operator long long() const;

	friend LN operator+(const LN& a, const LN& b);
	LN& operator+=(const LN& ln);

	friend LN operator-(const LN& a, const LN& b);
	LN& operator-=(const LN& ln);

	friend LN operator*(const LN& a, const LN& b);
	LN& operator*=(const LN& ln);

	friend LN operator/(const LN& a, const LN& b);
	LN& operator/=(const LN& ln);

	friend LN operator%(const LN& a, const LN& b);
	LN& operator%=(const LN& ln);

	LN operator-() const;
	LN operator~() const;

	friend bool operator>(const LN& a, const LN& b);
	friend bool operator>=(const LN& a, const LN& b);
	friend bool operator<(const LN& a, const LN& b);
	friend bool operator<=(const LN& a, const LN& b);
	friend bool operator==(const LN& a, const LN& b);
	friend bool operator!=(const LN& a, const LN& b);

	LN& operator=(const LN& ln);
	LN& operator=(LN&& ln) noexcept;

	void convert(std::ostream& out);

  private:
	bool m_isNeg{};
	bool m_isNan{};
	my_vector m_value;
	explicit LN(const my_vector& value);
	explicit LN(my_vector& value, bool negate, bool nan);
	[[nodiscard]] size_t size() const { return m_value.size(); }
	static my_vector add(const LN& a, const LN& b);
	static my_vector sub(const LN& a, const LN& b);
	static int compare(const LN& a, const LN& b);
};

LN operator"" _ln(const char val[]);
