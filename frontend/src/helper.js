/**
 * Converts a `YYYY-MM-DD` date format to `DD.MM.YYYY`.
 *
 * @param {string} date the date to convert
 * @returns {string} the converted date
 */
export default function formatDate(date) {
  if (date.length === 10) {
    const year = date.substring(0, 4);
    const month = date.substring(5, 7);
    const day = date.substring(8, 10);

    return `${day}.${month}.${year}`;
  }
  return '';
}
