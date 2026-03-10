import styled from 'styled-components'

export const Page = styled.div`
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 24px;
`

export const Form = styled.form`
  display: flex;
  align-items: center;
  gap: 12px;
`

export const Input = styled.input`
  padding: 6px 10px;
`

export const CheckboxLabel = styled.label`
  display: flex;
  align-items: center;
  gap: 6px;
`

export const Button = styled.button`
  padding: 6px 14px;
  cursor: pointer;

  &:disabled {
    opacity: 0.5;
    cursor: default;
  }
`

export const TableWrapper = styled.div`
  display: flex;
  align-items: flex-start;
  gap: 16px;
`

export const Actions = styled.div`
  display: flex;
  flex-direction: column;
  gap: 8px;
`

export const Table = styled.table`
  border-collapse: collapse;

  th,
  td {
    border: 1px solid #ccc;
    padding: 8px 12px;
    text-align: left;
  }

  thead tr {
    background-color: #f0f0f0;
  }

  tbody tr + tr td {
    border-top: 1px solid #ccc;
  }
`
