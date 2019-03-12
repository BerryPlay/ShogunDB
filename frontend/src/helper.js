/**
 * Converts a `YYYY-MM-DD` date format to `DD.MM.YYYY`.
 *
 * @param {string} date the date to convert
 * @returns {string} the converted date
 */
export function formatDate(date) {
  if (date.length === 10) {
    const year = date.substring(0, 4);
    const month = date.substring(5, 7);
    const day = date.substring(8, 10);

    return `${day}.${month}.${year}`;
  }
  return '';
}

/**
 * Calculates the age for the given date of birth.
 *
 * @param {string} dateOfBirth the date of birth (format: 'yyyy-mm-ddd')
 * @returns {number} the age
 */
export function calculateAge(dateOfBirth) {
  // get the dates
  const today = new Date();
  const birthDate = new Date(dateOfBirth);

  let age = today.getFullYear() - birthDate.getFullYear();
  const m = today.getMonth() - birthDate.getMonth();
  if (m < 0 || (m === 0 && today.getDate() < birthDate.getDate())) {
    age -= 1;
  }
  return age;
}
